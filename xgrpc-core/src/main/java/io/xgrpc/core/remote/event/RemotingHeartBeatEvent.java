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

package io.xgrpc.core.remote.event;

import io.xgrpc.common.notify.Event;

/**
 * Remoting connection heart beat event.
 *
 * @author xiweng.yy
 */
public class RemotingHeartBeatEvent extends Event {
    
    private final String connectionId;
    
    private final String clientIp;
    
    private final String clientVersion;
    
    public RemotingHeartBeatEvent(String connectionId, String clientIp, String clientVersion) {
        this.connectionId = connectionId;
        this.clientIp = clientIp;
        this.clientVersion = clientVersion;
    }
    
    public String getConnectionId() {
        return connectionId;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public String getClientVersion() {
        return clientVersion;
    }
}
