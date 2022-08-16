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

package io.xgrpc.core.remote.control;

import io.xgrpc.api.common.Constants;

/**
 * MonitorType.
 *
 * @author liuzunfei
 * @version $Id: MonitorKey.java, v 0.1 2021年01月20日 20:38 PM liuzunfei Exp $
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class MonitorKey {
    
    String key;
    
    public MonitorKey() {
    
    }
    
    public MonitorKey(String key) {
        this.key = key;
    }
    
    /**
     * get monitor key type.
     *
     * @return type.
     */
    public abstract String getType();
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String build() {
        return this.getType() + Constants.COLON + this.getKey();
    }
}
