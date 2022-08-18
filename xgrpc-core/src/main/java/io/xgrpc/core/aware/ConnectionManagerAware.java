package io.xgrpc.core.aware;

import io.xgrpc.core.remote.connection.ConnectionManager;

public interface ConnectionManagerAware {
    /**
     * 设置连接管理器
     *
     * @param connectionManager 连接管理器
     */
    void setConnectionManager(ConnectionManager connectionManager);
}
