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

import io.xgrpc.plugin.auth.api.IdentityContext;
import io.xgrpc.plugin.auth.api.Permission;
import io.xgrpc.plugin.auth.api.Resource;
import io.xgrpc.plugin.auth.constant.ActionTypes;
import io.xgrpc.plugin.auth.exception.AccessException;
import io.xgrpc.plugin.auth.spi.server.AuthPluginService;

import java.util.Collection;
import java.util.Collections;

/**
 * Mock Server Auth Plugin Service.
 *
 * @author xiweng.yy
 */
public class MockAuthPluginService implements AuthPluginService {
    
    @Override
    public Collection<String> identityNames() {
        return Collections.singletonList("mock");
    }
    
    @Override
    public boolean enableAuth(ActionTypes action, String type) {
        return false;
    }
    
    @Override
    public boolean validateIdentity(IdentityContext identityContext, Resource resource) throws AccessException {
        return false;
    }
    
    @Override
    public Boolean validateAuthority(IdentityContext identityContext, Permission permission) throws AccessException {
        return false;
    }
    
    @Override
    public String getAuthServiceName() {
        return "mock";
    }
}
