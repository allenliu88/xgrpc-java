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

package io.xgrpc.core.monitor;

import java.util.ArrayList;
import java.util.List;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

/**
 * Metrics unified usage center.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
@SuppressWarnings("all")
public final class XgrpcMeterRegistry {
    
    private static final CompositeMeterRegistry METER_REGISTRY = new CompositeMeterRegistry();
    
    public static DistributionSummary summary(String module, String name) {
        ImmutableTag moduleTag = new ImmutableTag("module", module);
        List<Tag> tags = new ArrayList<>();
        tags.add(moduleTag);
        tags.add(new ImmutableTag("name", name));
        return METER_REGISTRY.summary("xgrpc_monitor", tags);
    }
    
    public static Timer timer(String module, String name) {
        ImmutableTag moduleTag = new ImmutableTag("module", module);
        List<Tag> tags = new ArrayList<>();
        tags.add(moduleTag);
        tags.add(new ImmutableTag("name", name));
        return METER_REGISTRY.timer("xgrpc_monitor", tags);
    }
    
    public static CompositeMeterRegistry getMeterRegistry() {
        return METER_REGISTRY;
    }
}
