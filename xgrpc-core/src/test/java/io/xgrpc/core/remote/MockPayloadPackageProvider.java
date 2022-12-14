/*
 * Copyright 1999-2021 Xgrpc Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.xgrpc.core.remote;

import java.util.HashSet;
import java.util.Set;

import io.xgrpc.common.remote.PayloadPackageProvider;

/**
 * test package provider.
 *
 * @author hujun
 */
public class MockPayloadPackageProvider implements PayloadPackageProvider {
    
    private final Set<String> scanPackage = new HashSet<>();
    
    {
        scanPackage.add("io.xgrpc.api.naming.remote.request");
        scanPackage.add("io.xgrpc.api.remote.request");
        scanPackage.add("io.xgrpc.naming.cluster.remote.request");
        scanPackage.add("io.xgrpc.api.config.remote.request");
        scanPackage.add("io.xgrpc.api.naming.remote.response");
        scanPackage.add("io.xgrpc.api.config.remote.response");
        scanPackage.add("io.xgrpc.api.remote.response");
        scanPackage.add("io.xgrpc.naming.cluster.remote.response");
    }
    
    @Override
    public Set<String> getScanPackage() {
        return scanPackage;
    }
}
