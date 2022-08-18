package io.xgrpc.core;

import io.xgrpc.api.exception.runtime.XgrpcRuntimeException;
import io.xgrpc.common.remote.ConnectionType;
import io.xgrpc.core.remote.grpc.BaseGrpcServer;

public class GrpcServerBootstrap {
    private static final BaseGrpcServer baseGrpcServer = GuiceInjectorBootstrap.getBean(BaseGrpcServer.class);

    /**
     * 启动服务器
     */
    public static void startServer() {
        try {
            baseGrpcServer.start();
        } catch (Exception ex) {
            throw new XgrpcRuntimeException(ex);
        }
    }

    /**
     * 停止服务器
     */
    public static void stopServer() {
        try {
            baseGrpcServer.stopServer();
        } catch (Exception ex) {
            throw new XgrpcRuntimeException(ex);
        }
    }

    public static ConnectionType getConnectionType() {
        return baseGrpcServer.getConnectionType();
    }

    public static int rpcPortOffset() {
        return baseGrpcServer.rpcPortOffset();
    }
}
