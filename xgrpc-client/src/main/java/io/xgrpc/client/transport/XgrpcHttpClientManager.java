/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.xgrpc.client.transport;

import static io.xgrpc.client.utils.LogUtils.NAMING_LOGGER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import io.xgrpc.api.common.Constants;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.client.utils.LogUtils;
import io.xgrpc.client.utils.ParamUtil;
import io.xgrpc.common.http.AbstractHttpClientFactory;
import io.xgrpc.common.http.HttpClientBeanHolder;
import io.xgrpc.common.http.HttpClientConfig;
import io.xgrpc.common.http.HttpClientFactory;
import io.xgrpc.common.http.client.HttpClientRequestInterceptor;
import io.xgrpc.common.http.client.XgrpcRestTemplate;
import io.xgrpc.common.http.client.response.HttpClientResponse;
import io.xgrpc.common.http.param.Header;
import io.xgrpc.common.lifecycle.Closeable;
import io.xgrpc.common.model.RequestHttpEntity;
import io.xgrpc.common.utils.ExceptionUtil;
import io.xgrpc.common.utils.JacksonUtils;
import io.xgrpc.common.utils.MD5Utils;
import org.slf4j.Logger;

/**
 * config http Manager.
 *
 * @author mai.jh
 */
public class XgrpcHttpClientManager implements Closeable {
    
    private static final Logger LOGGER = LogUtils.logger(XgrpcHttpClientManager.class);
    
    private static final HttpClientFactory HTTP_CLIENT_FACTORY = new ConfigHttpClientFactory();
    
    private static final int CON_TIME_OUT_MILLIS = ParamUtil.getConnectTimeout();
    
    private static final int READ_TIME_OUT_MILLIS = 3000;
    
    private static final XgrpcRestTemplate XGRPC_REST_TEMPLATE;
    
    static {
        XGRPC_REST_TEMPLATE = HttpClientBeanHolder.getXgrpcRestTemplate(HTTP_CLIENT_FACTORY);
        XGRPC_REST_TEMPLATE.getInterceptors().add(new LimiterHttpClientRequestInterceptor());
    }
    
    private static class ConfigHttpClientManagerInstance {
        
        private static final XgrpcHttpClientManager INSTANCE = new XgrpcHttpClientManager();
    }
    
    public static XgrpcHttpClientManager getInstance() {
        return ConfigHttpClientManagerInstance.INSTANCE;
    }
    
    @Override
    public void shutdown() throws XgrpcException {
        NAMING_LOGGER.warn("[ConfigHttpClientManager] Start destroying xgrpcRestTemplate");
        try {
            HttpClientBeanHolder.shutdownXgrpcSyncRest(HTTP_CLIENT_FACTORY.getClass().getName());
        } catch (Exception ex) {
            NAMING_LOGGER.error("[ConfigHttpClientManager] An exception occurred when the HTTP client was closed : {}",
                    ExceptionUtil.getStackTrace(ex));
        }
        NAMING_LOGGER.warn("[ConfigHttpClientManager] Destruction of the end");
    }
    
    /**
     * get connectTimeout.
     *
     * @param connectTimeout connectTimeout
     * @return int return max timeout
     */
    public int getConnectTimeoutOrDefault(int connectTimeout) {
        return Math.max(CON_TIME_OUT_MILLIS, connectTimeout);
    }
    
    /**
     * get XgrpcRestTemplate Instance.
     *
     * @return XgrpcRestTemplate
     */
    public XgrpcRestTemplate getXgrpcRestTemplate() {
        return XGRPC_REST_TEMPLATE;
    }
    
    /**
     * ConfigHttpClientFactory.
     */
    private static class ConfigHttpClientFactory extends AbstractHttpClientFactory {
        
        @Override
        protected HttpClientConfig buildHttpClientConfig() {
            return HttpClientConfig.builder().setConTimeOutMillis(CON_TIME_OUT_MILLIS)
                    .setReadTimeOutMillis(READ_TIME_OUT_MILLIS).build();
        }
        
        @Override
        protected Logger assignLogger() {
            return LOGGER;
        }
    }
    
    /**
     * config Limiter implement.
     */
    private static class LimiterHttpClientRequestInterceptor implements HttpClientRequestInterceptor {
        
        @Override
        public boolean isIntercept(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity) {
            final String body = requestHttpEntity.getBody() == null ? "" : JacksonUtils.toJson(requestHttpEntity.getBody());
            return Limiter.isLimit(MD5Utils.md5Hex(uri + body, Constants.ENCODE));
        }
        
        @Override
        public HttpClientResponse intercept() {
            return new LimitResponse();
        }
    }
    
    /**
     * Limit Interrupt response.
     */
    private static class LimitResponse implements HttpClientResponse {
        
        @Override
        public Header getHeaders() {
            return Header.EMPTY;
        }
        
        @Override
        public InputStream getBody() throws IOException {
            return new ByteArrayInputStream("More than client-side current limit threshold".getBytes());
        }
        
        @Override
        public int getStatusCode() {
            return XgrpcException.CLIENT_OVER_THRESHOLD;
        }
        
        @Override
        public String getStatusText() {
            return null;
        }
        
        @Override
        public void close() {
        
        }
    }
}
