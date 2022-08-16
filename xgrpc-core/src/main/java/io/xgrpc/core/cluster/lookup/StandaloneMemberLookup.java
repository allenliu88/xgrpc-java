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

package io.xgrpc.core.cluster.lookup;

import java.util.Collections;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.core.cluster.AbstractMemberLookup;
import io.xgrpc.core.cluster.MemberUtil;
import io.xgrpc.sys.env.EnvUtil;

/**
 * Member node addressing mode in stand-alone mode.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class StandaloneMemberLookup extends AbstractMemberLookup {
    
    @Override
    public void doStart() {
        String url = EnvUtil.getLocalAddress();
        afterLookup(MemberUtil.readServerConf(Collections.singletonList(url)));
    }
    
    @Override
    protected void doDestroy() throws XgrpcException {
    
    }
    
    @Override
    public boolean useAddressServer() {
        return false;
    }
}
