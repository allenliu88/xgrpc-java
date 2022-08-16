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

package io.xgrpc.plugin.auth.spi.client;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.common.http.client.NacosRestTemplate;
import io.xgrpc.common.lifecycle.Closeable;
import io.xgrpc.common.spi.NacosServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClientAuthService classLoader.
 *
 * @author wuyfee
 */
public class ClientAuthPluginManager implements Closeable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthPluginManager.class);
    
    /**
     * The relationship of context type and {@link ClientAuthService}.
     */
    private final Set<ClientAuthService> clientAuthServiceHashSet = new HashSet<>();
    
    /**
     * init ClientAuthService.
     */
    public void init(List<String> serverList, NacosRestTemplate nacosRestTemplate) {
        
        Collection<AbstractClientAuthService> clientAuthServices = NacosServiceLoader
                .load(AbstractClientAuthService.class);
        for (ClientAuthService clientAuthService : clientAuthServices) {
            clientAuthService.setServerList(serverList);
            clientAuthService.setNacosRestTemplate(nacosRestTemplate);
            clientAuthServiceHashSet.add(clientAuthService);
            LOGGER.info("[ClientAuthPluginManager] Load ClientAuthService {} success.",
                    clientAuthService.getClass().getCanonicalName());
        }
        if (clientAuthServiceHashSet.isEmpty()) {
            LOGGER.warn("[ClientAuthPluginManager] Load ClientAuthService fail, No ClientAuthService implements");
        }
    }
    
    /**
     * get all ClientAuthService instance.
     *
     * @return ClientAuthService Set.
     */
    public Set<ClientAuthService> getAuthServiceSpiImplSet() {
        return clientAuthServiceHashSet;
    }
    
    @Override
    public void shutdown() throws XgrpcException {
        for (ClientAuthService each : clientAuthServiceHashSet) {
            each.shutdown();
        }
    }
}
