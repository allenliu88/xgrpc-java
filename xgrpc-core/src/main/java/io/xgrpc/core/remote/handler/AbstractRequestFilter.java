/*
 * Copyright 1999-2020 Xgrpc Holding Ltd.
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

package io.xgrpc.core.remote.handler;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.response.Response;

/**
 * interceptor fo request.
 *
 * @author liuzunfei
 * @version $Id: AbstractRequestFilter.java, v 0.1 2020年09月14日 11:46 AM liuzunfei Exp $
 */
public abstract class AbstractRequestFilter {
    public AbstractRequestFilter() {
    }
    
    protected Class getResponseClazz(Class handlerClazz) throws XgrpcException {
        ParameterizedType parameterizedType = (ParameterizedType) handlerClazz.getGenericSuperclass();
        try {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return Class.forName(actualTypeArguments[1].getTypeName());
            
        } catch (Exception e) {
            throw new XgrpcException(XgrpcException.SERVER_ERROR, e);
        }
    }
    
    protected Method getHandleMethod(Class handlerClazz) throws XgrpcException {
        try {
            Method method = handlerClazz.getMethod("handle", Request.class, RequestMeta.class);
            return method;
        } catch (NoSuchMethodException e) {
            throw new XgrpcException(XgrpcException.SERVER_ERROR, e);
        }
    }
    
    protected <T> Response getDefaultResponseInstance(Class handlerClazz) throws XgrpcException {
        ParameterizedType parameterizedType = (ParameterizedType) handlerClazz.getGenericSuperclass();
        try {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return (Response) Class.forName(actualTypeArguments[1].getTypeName()).newInstance();
            
        } catch (Exception e) {
            throw new XgrpcException(XgrpcException.SERVER_ERROR, e);
        }
    }
    
    /**
     * filter request.
     *
     * @param request      request.
     * @param meta         request meta.
     * @param handlerClazz request handler clazz.
     * @return response
     * @throws XgrpcException XgrpcException.
     */
    protected abstract Response filter(Request request, RequestMeta meta, Class handlerClazz) throws XgrpcException;
}
