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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.common.utils.StringUtils;
import io.xgrpc.core.cluster.AbstractMemberLookup;
import io.xgrpc.core.cluster.Member;
import io.xgrpc.core.cluster.MemberUtil;
import io.xgrpc.core.utils.Loggers;
import io.xgrpc.sys.env.EnvUtil;
import io.xgrpc.sys.file.FileChangeEvent;
import io.xgrpc.sys.file.FileWatcher;
import io.xgrpc.sys.file.WatchFileCenter;

/**
 * Cluster.conf file managed cluster member node addressing pattern.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class FileConfigMemberLookup extends AbstractMemberLookup {
    
    private static final String DEFAULT_SEARCH_SEQ = "cluster.conf";
    
    private FileWatcher watcher = new FileWatcher() {
        @Override
        public void onChange(FileChangeEvent event) {
            readClusterConfFromDisk();
        }
        
        @Override
        public boolean interest(String context) {
            return StringUtils.contains(context, DEFAULT_SEARCH_SEQ);
        }
    };
    
    @Override
    public void doStart() throws XgrpcException {
        readClusterConfFromDisk();
        
        // Use the inotify mechanism to monitor file changes and automatically
        // trigger the reading of cluster.conf
        try {
            WatchFileCenter.registerWatcher(EnvUtil.getConfPath(), watcher);
        } catch (Throwable e) {
            Loggers.CLUSTER.error("An exception occurred in the launch file monitor : {}", e.getMessage());
        }
    }
    
    @Override
    public boolean useAddressServer() {
        return false;
    }
    
    @Override
    protected void doDestroy() throws XgrpcException {
        WatchFileCenter.deregisterWatcher(EnvUtil.getConfPath(), watcher);
    }
    
    private void readClusterConfFromDisk() {
        Collection<Member> tmpMembers = new ArrayList<>();
        try {
            List<String> tmp = EnvUtil.readClusterConf();
            tmpMembers = MemberUtil.readServerConf(tmp);
        } catch (Throwable e) {
            Loggers.CLUSTER
                    .error("nacos-XXXX [serverlist] failed to get serverlist from disk!, error : {}", e.getMessage());
        }
        
        afterLookup(tmpMembers);
    }
}
