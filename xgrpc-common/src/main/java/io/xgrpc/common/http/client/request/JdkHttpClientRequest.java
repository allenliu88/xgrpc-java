/*
 * Copyright 1999-2018 Xgrpc Holding Ltd.
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

package io.xgrpc.common.http.client.request;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import io.xgrpc.common.constant.HttpHeaderConsts;
import io.xgrpc.common.http.HttpClientConfig;
import io.xgrpc.common.http.HttpUtils;
import io.xgrpc.common.http.client.response.HttpClientResponse;
import io.xgrpc.common.http.client.response.JdkHttpClientResponse;
import io.xgrpc.common.http.param.Header;
import io.xgrpc.common.http.param.MediaType;
import io.xgrpc.common.model.RequestHttpEntity;
import io.xgrpc.common.utils.IoUtils;
import io.xgrpc.common.utils.JacksonUtils;

/**
 * JDK http client request implement.
 *
 * @author mai.jh
 */
public class JdkHttpClientRequest implements HttpClientRequest {
    
    private static final String CONTENT_LENGTH = "Content-Length";
    
    private HttpClientConfig httpClientConfig;
    
    public JdkHttpClientRequest(HttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }
    
    /**
     * Use specified {@link SSLContext}.
     *
     * @param sslContext ssl context
     */
    @SuppressWarnings("checkstyle:abbreviationaswordinname")
    public void setSSLContext(SSLContext sslContext) {
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }
    }
    
    /**
     * Replace the default HostnameVerifier.
     *
     * @param hostnameVerifier custom hostnameVerifier
     */
    @SuppressWarnings("checkstyle:abbreviationaswordinname")
    public void replaceSSLHostnameVerifier(HostnameVerifier hostnameVerifier) {
        if (hostnameVerifier != null) {
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        }
    }
    
    @Override
    public HttpClientResponse execute(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity)
            throws Exception {
        final Object body = requestHttpEntity.getBody();
        final Header headers = requestHttpEntity.getHeaders();
        replaceDefaultConfig(requestHttpEntity.getHttpClientConfig());
        
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        Map<String, String> headerMap = headers.getHeader();
        if (headerMap != null && headerMap.size() > 0) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        
        conn.setConnectTimeout(this.httpClientConfig.getConTimeOutMillis());
        conn.setReadTimeout(this.httpClientConfig.getReadTimeOutMillis());
        conn.setRequestMethod(httpMethod);
        if (body != null && !"".equals(body)) {
            String contentType = headers.getValue(HttpHeaderConsts.CONTENT_TYPE);
            String bodyStr = JacksonUtils.toJson(body);
            if (MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
                Map<String, String> map = JacksonUtils.toObj(bodyStr, HashMap.class);
                bodyStr = HttpUtils.encodingParams(map, headers.getCharset());
            }
            if (bodyStr != null) {
                conn.setDoOutput(true);
                byte[] b = bodyStr.getBytes();
                conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(b.length));
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(b, 0, b.length);
                outputStream.flush();
                IoUtils.closeQuietly(outputStream);
            }
        }
        conn.connect();
        return new JdkHttpClientResponse(conn);
    }
    
    /**
     * Replace the HTTP config created by default with the HTTP config specified in the request.
     *
     * @param replaceConfig http config
     */
    private void replaceDefaultConfig(HttpClientConfig replaceConfig) {
        if (replaceConfig == null) {
            return;
        }
        this.httpClientConfig = replaceConfig;
    }
    
    @Override
    public void close() throws IOException {
    
    }
}
