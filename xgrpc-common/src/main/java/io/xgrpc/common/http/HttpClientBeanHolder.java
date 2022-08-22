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

package io.xgrpc.common.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.xgrpc.common.http.client.XgrpcAsyncRestTemplate;
import io.xgrpc.common.http.client.XgrpcRestTemplate;
import io.xgrpc.common.utils.ExceptionUtil;
import io.xgrpc.common.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a rest template to ensure that each custom client config and rest template are in one-to-one correspondence.
 *
 * @author mai.jh
 */
public final class HttpClientBeanHolder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientBeanHolder.class);
    
    private static final Map<String, XgrpcRestTemplate> SINGLETON_REST = new HashMap<>(10);
    
    private static final Map<String, XgrpcAsyncRestTemplate> SINGLETON_ASYNC_REST = new HashMap<>(
            10);
    
    private static final AtomicBoolean ALREADY_SHUTDOWN = new AtomicBoolean(false);
    
    static {
        ThreadUtils.addShutdownHook(HttpClientBeanHolder::shutdown);
    }
    
    public static XgrpcRestTemplate getXgrpcRestTemplate(Logger logger) {
        return getXgrpcRestTemplate(new DefaultHttpClientFactory(logger));
    }
    
    public static XgrpcRestTemplate getXgrpcRestTemplate(HttpClientFactory httpClientFactory) {
        if (httpClientFactory == null) {
            throw new NullPointerException("httpClientFactory is null");
        }
        String factoryName = httpClientFactory.getClass().getName();
        XgrpcRestTemplate xgrpcRestTemplate = SINGLETON_REST.get(factoryName);
        if (xgrpcRestTemplate == null) {
            synchronized (SINGLETON_REST) {
                xgrpcRestTemplate = SINGLETON_REST.get(factoryName);
                if (xgrpcRestTemplate != null) {
                    return xgrpcRestTemplate;
                }
                xgrpcRestTemplate = httpClientFactory.createXgrpcRestTemplate();
                SINGLETON_REST.put(factoryName, xgrpcRestTemplate);
            }
        }
        return xgrpcRestTemplate;
    }
    
    public static XgrpcAsyncRestTemplate getXgrpcAsyncRestTemplate(Logger logger) {
        return getXgrpcAsyncRestTemplate(new DefaultHttpClientFactory(logger));
    }
    
    public static XgrpcAsyncRestTemplate getXgrpcAsyncRestTemplate(HttpClientFactory httpClientFactory) {
        if (httpClientFactory == null) {
            throw new NullPointerException("httpClientFactory is null");
        }
        String factoryName = httpClientFactory.getClass().getName();
        XgrpcAsyncRestTemplate xgrpcAsyncRestTemplate = SINGLETON_ASYNC_REST.get(factoryName);
        if (xgrpcAsyncRestTemplate == null) {
            synchronized (SINGLETON_ASYNC_REST) {
                xgrpcAsyncRestTemplate = SINGLETON_ASYNC_REST.get(factoryName);
                if (xgrpcAsyncRestTemplate != null) {
                    return xgrpcAsyncRestTemplate;
                }
                xgrpcAsyncRestTemplate = httpClientFactory.createXgrpcAsyncRestTemplate();
                SINGLETON_ASYNC_REST.put(factoryName, xgrpcAsyncRestTemplate);
            }
        }
        return xgrpcAsyncRestTemplate;
    }
    
    /**
     * Shutdown common http client.
     */
    private static void shutdown() {
        if (!ALREADY_SHUTDOWN.compareAndSet(false, true)) {
            return;
        }
        LOGGER.warn("[HttpClientBeanHolder] Start destroying common HttpClient");
        try {
            shutdown(DefaultHttpClientFactory.class.getName());
        } catch (Exception ex) {
            LOGGER.error("An exception occurred when the common HTTP client was closed : {}", ExceptionUtil.getStackTrace(ex));
        }
        LOGGER.warn("[HttpClientBeanHolder] Destruction of the end");
    }
    
    /**
     * Shutdown http client holder and close remove template.
     *
     * @param className HttpClientFactory implement class name
     * @throws Exception ex
     */
    public static void shutdown(String className) throws Exception {
        shutdownXgrpcSyncRest(className);
        shutdownXgrpcAsyncRest(className);
    }
    
    /**
     * Shutdown sync http client holder and remove template.
     *
     * @param className HttpClientFactory implement class name
     * @throws Exception ex
     */
    public static void shutdownXgrpcSyncRest(String className) throws Exception {
        final XgrpcRestTemplate xgrpcRestTemplate = SINGLETON_REST.get(className);
        if (xgrpcRestTemplate != null) {
            xgrpcRestTemplate.close();
            SINGLETON_REST.remove(className);
        }
    }
    
    /**
     * Shutdown async http client holder and remove template.
     *
     * @param className HttpClientFactory implement class name
     * @throws Exception ex
     */
    public static void shutdownXgrpcAsyncRest(String className) throws Exception {
        final XgrpcAsyncRestTemplate xgrpcAsyncRestTemplate = SINGLETON_ASYNC_REST.get(className);
        if (xgrpcAsyncRestTemplate != null) {
            xgrpcAsyncRestTemplate.close();
            SINGLETON_ASYNC_REST.remove(className);
        }
    }
}
