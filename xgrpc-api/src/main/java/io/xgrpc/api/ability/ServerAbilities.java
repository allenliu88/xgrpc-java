/*
 * Copyright 1999-2018 Xgrpc Holding Ltd.
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
import java.util.Objects;

import io.xgrpc.api.remote.ability.ServerRemoteAbility;

/**
 * abilities of xgrpc server.
 *
 * @author liuzunfei
 * @version $Id: ServerAbilities.java, v 0.1 2021年01月24日 00:09 AM liuzunfei Exp $
 */
public class ServerAbilities implements Serializable {
    
    private static final long serialVersionUID = -2120543002911304171L;
    
    private ServerRemoteAbility remoteAbility = new ServerRemoteAbility();
    
    public ServerRemoteAbility getRemoteAbility() {
        return remoteAbility;
    }
    
    public void setRemoteAbility(ServerRemoteAbility remoteAbility) {
        this.remoteAbility = remoteAbility;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerAbilities that = (ServerAbilities) o;
        return Objects.equals(remoteAbility, that.remoteAbility);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(remoteAbility);
    }
}
