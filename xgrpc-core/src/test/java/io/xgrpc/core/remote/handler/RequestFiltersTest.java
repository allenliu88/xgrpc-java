/*
 *  Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.xgrpc.core.remote.handler;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.response.Response;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link RequestFilters} unit test.
 *
 * @author chenglu
 * @date 2021-07-02 19:20
 */
public class RequestFiltersTest {
    
    @Test
    public void testRegisterFilter() {
        RequestFilters requestFilters = RequestFilters.getInstance();
        requestFilters.registerFilter(new AbstractRequestFilter() {
            @Override
            protected Response filter(Request request, RequestMeta meta, Class handlerClazz) throws XgrpcException {
                return null;
            }
        });
    
        Assert.assertEquals(1, requestFilters.filters.size());
    }
}
