/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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

package io.xgrpc.core.remote.registry;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.common.utils.ServiceLoaderUtils;
import io.xgrpc.core.GuiceInjectorBootstrap;
import io.xgrpc.core.aware.ConnectionManagerAware;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.core.remote.handler.RequestHandler;

/**
 * RequestHandlerRegistry.
 *
 * @author liuzunfei
 * @version $Id: RequestHandlerRegistry.java, v 0.1 2020年07月13日 8:24 PM liuzunfei Exp $
 */
public class RequestHandlerRegistry {
    Map<String, RequestHandler> registryHandlers = new HashMap<>();
    
    /**
     * Get Request Handler By request Type.
     *
     * @param requestType see definitions  of sub constants classes of RequestTypeConstants
     * @return request handler.
     */
    public RequestHandler getByRequestType(String requestType) {
        return registryHandlers.get(requestType);
    }
    
    @PostConstruct
    public void init() {
        List<RequestHandler> requestHandlerList = ServiceLoaderUtils.load(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlerList) {
            // 设置连接管理器
            if (requestHandler instanceof ConnectionManagerAware) {
                ((ConnectionManagerAware) requestHandler).setConnectionManager(GuiceInjectorBootstrap.getBean(ConnectionManager.class));
            }
            Class<?> clazz = requestHandler.getClass();
            boolean skip = false;
            while (!clazz.getSuperclass().equals(RequestHandler.class)) {
                if (clazz.getSuperclass().equals(Object.class)) {
                    skip = true;
                    break;
                }
                clazz = clazz.getSuperclass();
            }
            if (skip) {
                continue;
            }
            
            try {
                Method method = clazz.getMethod("handle", Request.class, RequestMeta.class);
            } catch (Exception e) {
                //ignore.
            }
            Class tClass = (Class) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
            registryHandlers.putIfAbsent(tClass.getSimpleName(), requestHandler);
        }
    }
}
