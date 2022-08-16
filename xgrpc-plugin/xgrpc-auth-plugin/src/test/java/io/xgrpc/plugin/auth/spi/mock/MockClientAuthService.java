/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
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

package io.xgrpc.plugin.auth.spi.mock;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.plugin.auth.api.LoginIdentityContext;
import io.xgrpc.plugin.auth.api.RequestResource;
import io.xgrpc.plugin.auth.spi.client.AbstractClientAuthService;

import java.util.Properties;

public class MockClientAuthService extends AbstractClientAuthService {
    
    @Override
    public Boolean login(Properties properties) {
        return true;
    }
    
    @Override
    public LoginIdentityContext getLoginIdentityContext(RequestResource resource) {
        return null;
    }
    
    @Override
    public void shutdown() throws XgrpcException {
    
    }
}
