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

package io.xgrpc.core.remote.grpc;

import java.util.concurrent.ThreadPoolExecutor;

import io.xgrpc.api.common.Constants;
import io.xgrpc.core.utils.GlobalExecutor;

/**
 * Grpc implementation as  a rpc server.
 *
 * @author liuzunfei
 * @version $Id: BaseGrpcServer.java, v 0.1 2020年07月13日 3:42 PM liuzunfei Exp $
 */
public class GrpcClusterServer extends BaseGrpcServer {
    
    @Override
    public int rpcPortOffset() {
        return Constants.CLUSTER_GRPC_PORT_DEFAULT_OFFSET;
    }
    
    @Override
    public ThreadPoolExecutor getRpcExecutor() {
        if (!GlobalExecutor.clusterRpcExecutor.allowsCoreThreadTimeOut()) {
            GlobalExecutor.clusterRpcExecutor.allowCoreThreadTimeOut(true);
        }
        return GlobalExecutor.clusterRpcExecutor;
    }
}
