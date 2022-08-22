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

package io.xgrpc.core.remote.grpc;

import io.xgrpc.common.remote.ConnectionType;
import io.xgrpc.core.GrpcServerBootstrap;
import io.xgrpc.core.remote.BaseRpcServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * {@link GrpcSdkServer} and {@link GrpcClusterServer} unit test.
 *
 * @author chenglu
 * @date 2021-06-30 14:32
 */
@RunWith(MockitoJUnitRunner.class)
public class GrpcServerTest {
    
    @Before
    public void setUp() {

    }
    
    @Test
    public void testGrpcSdkServer() throws Exception {
        GrpcServerBootstrap.startServer();

        Assert.assertEquals(GrpcServerBootstrap.getConnectionType(), ConnectionType.GRPC);
        
        Assert.assertEquals(GrpcServerBootstrap.rpcPortOffset(), 1000);

        GrpcServerBootstrap.stopServer();
    }
    
    @Test
    public void testGrpcClusterServer() throws Exception {
        BaseRpcServer grpcSdkServer = new GrpcClusterServer();
        grpcSdkServer.start();
        
        Assert.assertEquals(grpcSdkServer.getConnectionType(), ConnectionType.GRPC);
    
        Assert.assertEquals(grpcSdkServer.rpcPortOffset(), 1001);
    
        grpcSdkServer.stopServer();
    }
}
