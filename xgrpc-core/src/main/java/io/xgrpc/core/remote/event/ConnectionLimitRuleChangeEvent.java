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
 *  connection limit rule change event.
 *  @author zunfei.lzf
 */
public class ConnectionLimitRuleChangeEvent extends Event {
    
    String limitRule;
    
    public ConnectionLimitRuleChangeEvent(String limitRule) {
        this.limitRule = limitRule;
    }
    
    public String getLimitRule() {
        return limitRule;
    }
    
    public void setLimitRule(String limitRule) {
        this.limitRule = limitRule;
    }
}
