/*
 *  Copyright 1999-2021 Xgrpc Holding Ltd.
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

package io.xgrpc.core.remote.core;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.request.ServerLoaderInfoRequest;
import io.xgrpc.api.remote.response.ServerLoaderInfoResponse;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.core.remote.handler.ServerLoaderInfoRequestHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * {@link ServerLoaderInfoRequestHandler} unit test.
 *
 * @author chenglu
 * @date 2021-07-01 12:48
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerLoaderInfoRequestHandlerTest {
    
    @InjectMocks
    private ServerLoaderInfoRequestHandler handler;
    
    @Mock
    private ConnectionManager connectionManager;
    
    @Test
    public void testHandle() {
        Mockito.when(connectionManager.currentClientsCount()).thenReturn(1);
        Mockito.when(connectionManager.currentClientsCount(Mockito.any())).thenReturn(1);
        Mockito.when(connectionManager.getConnectionLimitRule()).thenReturn(null);
    
        ServerLoaderInfoRequest request = new ServerLoaderInfoRequest();
        RequestMeta meta = new RequestMeta();

        try {
            ServerLoaderInfoResponse response = handler.handle(request, meta);
            String sdkConCount = response.getMetricsValue("sdkConCount");
            Assert.assertEquals(sdkConCount, "1");
            
        } catch (XgrpcException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
