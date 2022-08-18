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

package io.xgrpc.core.remote.handler;

import java.util.HashMap;
import java.util.Map;

import com.google.auto.service.AutoService;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.RemoteConstants;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.request.ServerReloadRequest;
import io.xgrpc.api.remote.response.ServerReloadResponse;
import io.xgrpc.core.aware.ConnectionManagerAware;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.core.utils.Loggers;
import io.xgrpc.core.utils.RemoteUtils;

/**
 * server reload request handler.
 *
 * @author liuzunfei
 * @version $Id: ServerReloaderRequestHandler.java, v 0.1 2020年11月09日 4:38 PM liuzunfei Exp $
 */
@AutoService(RequestHandler.class)
public class ServerReloaderRequestHandler extends RequestHandler<ServerReloadRequest, ServerReloadResponse> implements ConnectionManagerAware {
    private ConnectionManager connectionManager;

    @Override
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public ServerReloadResponse handle(ServerReloadRequest request, RequestMeta meta) throws XgrpcException {
        ServerReloadResponse response = new ServerReloadResponse();
        Loggers.REMOTE.info("server reload request receive,reload count={},redirectServer={},requestIp={}",
                request.getReloadCount(), request.getReloadServer(), meta.getClientIp());
        int reloadCount = request.getReloadCount();
        Map<String, String> filter = new HashMap<>(2);
        filter.put(RemoteConstants.LABEL_SOURCE, RemoteConstants.LABEL_SOURCE_SDK);
        int sdkCount = connectionManager.currentClientsCount(filter);
        if (sdkCount <= reloadCount) {
            response.setMessage("ignore");
        } else {
            reloadCount = (int) Math.max(reloadCount, sdkCount * (1 - RemoteUtils.LOADER_FACTOR));
            connectionManager.loadCount(reloadCount, request.getReloadServer());
            response.setMessage("ok");
        }
        return response;
    }
}
