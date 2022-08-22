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

package io.xgrpc.core.remote;

import java.util.UUID;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.PushCallBack;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.common.remote.exception.ConnectionAlreadyClosedException;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.core.remote.grpc.GrpcConnection;
import io.xgrpc.core.remote.push.RpcPushService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * {@link RpcPushService} unit test.
 *
 * @author chenglu
 * @date 2021-07-02 19:35
 */
@RunWith(MockitoJUnitRunner.class)
public class RpcPushServiceTest {
    
    @InjectMocks
    private RpcPushService rpcPushService;
    
    @Mock
    private ConnectionManager connectionManager;
    
    @Mock
    private GrpcConnection grpcConnection;
    
    private String connectId = UUID.randomUUID().toString();
    
    @Test
    public void testPushWithCallback() {
        try {
            Mockito.when(connectionManager.getConnection(Mockito.any())).thenReturn(null);
            rpcPushService.pushWithCallback(connectId, null, new PushCallBack() {
                @Override
                public long getTimeout() {
                    return 0;
                }
    
                @Override
                public void onSuccess(Response response) {
                    System.out.println("success");
                }
    
                @Override
                public void onFail(Throwable e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testPushWithoutAck() {
        Mockito.when(connectionManager.getConnection(Mockito.any())).thenReturn(grpcConnection);
        try {
            Mockito.when(grpcConnection.request(Mockito.any(), Mockito.eq(3000L)))
                    .thenThrow(ConnectionAlreadyClosedException.class);
            rpcPushService.pushWithoutAck(connectId, null);
    
            Mockito.when(grpcConnection.request(Mockito.any(), Mockito.eq(3000L)))
                    .thenThrow(XgrpcException.class);
            rpcPushService.pushWithoutAck(connectId, null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        try {
            Mockito.when(grpcConnection.request(Mockito.any(), Mockito.eq(3000L))).thenReturn(Mockito.any());
            rpcPushService.pushWithoutAck(connectId, null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
