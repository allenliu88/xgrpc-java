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

import com.google.auto.service.AutoService;
import io.xgrpc.api.remote.request.HealthCheckRequest;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.response.HealthCheckResponse;

/**
 * push response  to clients.
 *
 * @author liuzunfei
 * @version $Id: PushService.java, v 0.1 2021年07月17日 1:12 PM liuzunfei Exp $
 */
@AutoService(RequestHandler.class)
public class HealthCheckRequestHandler extends RequestHandler<HealthCheckRequest, HealthCheckResponse> {
    @Override
    public HealthCheckResponse handle(HealthCheckRequest request, RequestMeta meta) {
        return new HealthCheckResponse();
    }
}
