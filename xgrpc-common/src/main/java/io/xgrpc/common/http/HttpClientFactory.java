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

import io.xgrpc.common.http.client.XgrpcAsyncRestTemplate;
import io.xgrpc.common.http.client.XgrpcRestTemplate;

/**
 * http Client Factory.
 *
 * @author mai.jh
 */
public interface HttpClientFactory {
    
    /**
     * create new xgrpc rest.
     *
     * @return XgrpcRestTemplate
     */
    XgrpcRestTemplate createXgrpcRestTemplate();
    
    /**
     * create new xgrpc async rest.
     *
     * @return XgrpcAsyncRestTemplate
     */
    XgrpcAsyncRestTemplate createXgrpcAsyncRestTemplate();
    
}
