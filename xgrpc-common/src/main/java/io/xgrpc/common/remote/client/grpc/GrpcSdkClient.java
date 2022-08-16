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

package io.xgrpc.common.remote.client.grpc;

import io.xgrpc.api.common.Constants;

/**
 * gRPC client for sdk.
 *
 * @author liuzunfei
 * @version $Id: GrpcSdkClient.java, v 0.1 2020年09月07日 11:05 AM liuzunfei Exp $
 */
public class GrpcSdkClient extends GrpcClient {
    
    /**
     * Empty constructor.
     *
     * @param name name of client.
     */
    public GrpcSdkClient(String name) {
        super(name);
    }
    
    @Override
    public int rpcPortOffset() {
        return Integer.parseInt(System.getProperty(XGRPC_SERVER_GRPC_PORT_OFFSET_KEY,
                String.valueOf(Constants.SDK_GRPC_PORT_DEFAULT_OFFSET)));
    }
    
}