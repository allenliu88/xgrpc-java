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

package io.xgrpc.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loggers for core.
 *
 * @author nkorange
 * @since 1.2.0
 */
public class Loggers {
    
    public static final Logger AUTH = LoggerFactory.getLogger("io.xgrpc.core.auth");
    
    public static final Logger CORE = LoggerFactory.getLogger("io.xgrpc.core");
    
    public static final Logger RAFT = LoggerFactory.getLogger("io.xgrpc.core.protocol.raft");
    
    public static final Logger DISTRO = LoggerFactory.getLogger("io.xgrpc.core.protocol.distro");
    
    public static final Logger CLUSTER = LoggerFactory.getLogger("io.xgrpc.core.cluster");
    
    public static final Logger REMOTE = LoggerFactory.getLogger("io.xgrpc.core.remote");
    
    public static final Logger REMOTE_PUSH = LoggerFactory.getLogger("io.xgrpc.core.remote.push");
    
    public static final Logger REMOTE_DIGEST = LoggerFactory.getLogger("io.xgrpc.core.remote.digest");
    
    public static final Logger TPS_CONTROL_DIGEST = LoggerFactory
            .getLogger("io.xgrpc.core.remote.control.digest");
    
    public static final Logger TPS_CONTROL = LoggerFactory.getLogger("io.xgrpc.core.remote.control");
    
    public static final Logger TPS_CONTROL_DETAIL = LoggerFactory.getLogger("io.xgrpc.core.remote.control.detail");
}
