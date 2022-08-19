package io.xgrpc.client.transport;

import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.common.remote.client.RpcClient;

public interface RpcClientManager {
    /**
     * 构造请求客户端
     *
     * @param taskId 任务ID
     * @return 客户端
     */
    RpcClient build(String taskId);
    /**
     * 发起请求
     *
     * @param rpcClientInner 客户端
     * @param request 请求
     * @param timeoutMills 超时时间
     * @return 响应
     */
    Response request(RpcClient rpcClientInner, Request request, long timeoutMills);

    /**
     * 关闭所有由该Manager产生的客户端
     */
    void shutdown();
}
