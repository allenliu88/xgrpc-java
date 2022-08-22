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

package io.xgrpc.common.http.client;

import java.net.URI;

import io.xgrpc.common.http.client.request.HttpClientRequest;
import io.xgrpc.common.http.client.response.HttpClientResponse;
import io.xgrpc.common.model.RequestHttpEntity;

/**
 * Intercepts client-side HTTP requests. Implementations of this interface can be.
 *
 * @author mai.jh
 */
public interface HttpClientRequestInterceptor {
    
    /**
     * is intercept.
     *
     * @param uri uri
     * @param httpMethod http method
     * @param requestHttpEntity request entity
     * @return boolean
     */
    boolean isIntercept(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity);
    
    /**
     * if isIntercept method is true Intercept the given request, and return a response Otherwise,
     * the {@link HttpClientRequest} will be used for execution.
     *
     * @return HttpClientResponse
     */
    HttpClientResponse intercept();
}
