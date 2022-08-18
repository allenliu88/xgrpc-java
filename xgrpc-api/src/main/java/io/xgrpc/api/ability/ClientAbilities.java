/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

package io.xgrpc.api.ability;

import java.io.Serializable;

import io.xgrpc.api.remote.ability.ClientRemoteAbility;

/**
 * abilities of xgrpc client.
 *
 * @author liuzunfei
 * @version $Id: ClientAbilities.java, v 0.1 2021年01月24日 00:09 AM liuzunfei Exp $
 */
public class ClientAbilities implements Serializable {
    
    private static final long serialVersionUID = -3590789441404549261L;
    
    private ClientRemoteAbility remoteAbility = new ClientRemoteAbility();
    
    public ClientRemoteAbility getRemoteAbility() {
        return remoteAbility;
    }
    
    public void setRemoteAbility(ClientRemoteAbility remoteAbility) {
        this.remoteAbility = remoteAbility;
    }
}
