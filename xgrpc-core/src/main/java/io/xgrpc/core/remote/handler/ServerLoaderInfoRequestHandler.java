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

package io.xgrpc.core.remote.handler;

import java.util.HashMap;
import java.util.Map;

import com.google.auto.service.AutoService;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.RemoteConstants;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.request.ServerLoaderInfoRequest;
import io.xgrpc.api.remote.response.ServerLoaderInfoResponse;
import io.xgrpc.common.utils.JacksonUtils;
import io.xgrpc.core.aware.ConnectionManagerAware;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.sys.env.EnvUtil;

/**
 * request handler to handle server loader info.
 *
 * @author liuzunfei
 * @version $Id: ServerLoaderInfoRequestHandler.java, v 0.1 2020年09月03日 2:51 PM liuzunfei Exp $
 */
@AutoService(RequestHandler.class)
public class ServerLoaderInfoRequestHandler extends RequestHandler<ServerLoaderInfoRequest, ServerLoaderInfoResponse> implements ConnectionManagerAware {
    private ConnectionManager connectionManager;

    @Override
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public ServerLoaderInfoResponse handle(ServerLoaderInfoRequest request, RequestMeta meta) throws XgrpcException {
        ServerLoaderInfoResponse serverLoaderInfoResponse = new ServerLoaderInfoResponse();
        serverLoaderInfoResponse.putMetricsValue("conCount", String.valueOf(connectionManager.currentClientsCount()));
        Map<String, String> filter = new HashMap<>(2);
        filter.put(RemoteConstants.LABEL_SOURCE, RemoteConstants.LABEL_SOURCE_SDK);
        serverLoaderInfoResponse
                .putMetricsValue("sdkConCount", String.valueOf(connectionManager.currentClientsCount(filter)));
        serverLoaderInfoResponse.putMetricsValue("limitRule", JacksonUtils.toJson(connectionManager.getConnectionLimitRule()));
        serverLoaderInfoResponse.putMetricsValue("load", String.valueOf(EnvUtil.getLoad()));
        serverLoaderInfoResponse.putMetricsValue("cpu", String.valueOf(EnvUtil.getCpu()));
        
        return serverLoaderInfoResponse;
    }
    
}
