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

package io.xgrpc.core.remote.push;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.inject.Inject;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.AbstractRequestCallBack;
import io.xgrpc.api.remote.PushCallBack;
import io.xgrpc.api.remote.request.ServerRequest;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.common.remote.exception.ConnectionAlreadyClosedException;
import io.xgrpc.core.remote.connection.Connection;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.core.utils.Loggers;

/**
 * push response  to clients.
 *
 * @author liuzunfei
 * @version $Id: PushService.java, v 0.1 2020年07月20日 1:12 PM liuzunfei Exp $
 */
public class RpcPushService {
    @Inject
    private ConnectionManager connectionManager;
    
    /**
     * push response with no ack.
     *
     * @param connectionId    connectionId.
     * @param request         request.
     * @param requestCallBack requestCallBack.
     */
    public void pushWithCallback(String connectionId, ServerRequest request, PushCallBack requestCallBack, Executor executor) {
        Connection connection = connectionManager.getConnection(connectionId);
        this.pushWithCallbackInternal(connection, request, requestCallBack, executor);
    }

    /**
     * push response with no ack.
     *
     * @param clientIdentifyLabels    labels to identify clients.
     * @param request         request.
     * @param requestCallBack requestCallBack.
     */
    public void pushWithCallback(Map<String, String> clientIdentifyLabels, ServerRequest request, PushCallBack requestCallBack, Executor executor) {
        List<Connection> connectionList = connectionManager.getConnectionByLabels(clientIdentifyLabels);
        connectionList.forEach(connection -> this.pushWithCallbackInternal(connection, request, requestCallBack, executor));
    }

    private void pushWithCallbackInternal(
            Connection connection,
            ServerRequest request,
            PushCallBack requestCallBack,
            Executor executor) {
        String connectionId = connection.getMetaInfo().getConnectionId();

        if (connection == null) {
            requestCallBack.onSuccess(null);
            return;
        }

        // request with call back
        try {
            connection.asyncRequest(request, new AbstractRequestCallBack(requestCallBack.getTimeout()) {
                @Override
                public Executor getExecutor() {
                    return executor;
                }

                @Override
                public void onResponse(Response response) {
                    if (response.isSuccess()) {
                        requestCallBack.onSuccess(response);
                    } else {
                        requestCallBack.onFail(new XgrpcException(response.getErrorCode(), response.getMessage()));
                    }
                }

                @Override
                public void onException(Throwable e) {
                    requestCallBack.onFail(e);
                }
            });
        } catch (ConnectionAlreadyClosedException e) {
            connectionManager.unregister(connectionId);
            requestCallBack.onSuccess(null);
        } catch (Exception e) {
            Loggers.REMOTE_DIGEST
                    .error("error to send push response to connectionId ={},push response={}", connectionId,
                            request, e);
            requestCallBack.onFail(e);
        }
    }
    
    /**
     * push response with no ack.
     *
     * @param connectionId connectionId.
     * @param request      request.
     */
    public Response pushWithoutAck(String connectionId, ServerRequest request) {
        Connection connection = connectionManager.getConnection(connectionId);
        return this.pushWithoutAckInternal(connection, request);
    }

    public Map<String, Response> pushWithoutAck(Map<String, String> clientIdentifyLabels, ServerRequest request) {
        List<Connection> connectionList = connectionManager.getConnectionByLabels(clientIdentifyLabels);

        Map<String, Response> ret = new HashMap<>(2);
        connectionList.forEach(connection -> ret.put(connection.getMetaInfo().getConnectionId(), this.pushWithoutAckInternal(connection, request)));
        return ret;
    }

    private Response pushWithoutAckInternal(Connection connection, ServerRequest request) {
        if (connection == null) {
            return null;
        }

        // request
        String connectionId = connection.getMetaInfo().getConnectionId();
        try {
            return connection.request(request, 3000L);
        } catch (ConnectionAlreadyClosedException e) {
            connectionManager.unregister(connectionId);
        } catch (Exception e) {
            Loggers.REMOTE_DIGEST
                    .error("error to send push response to connectionId ={},push response={}", connectionId,
                            request, e);
        }

        return null;
    }
}
