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

package io.xgrpc.common.cache.decorators;

import java.util.concurrent.Callable;

import io.xgrpc.common.cache.Cache;

/**
 * A wrapper that thread-safe cache.
 * @author zzq
 * @date 2021/7/30
 */
public class SynchronizedCache<K, V> implements Cache<K, V> {
    
    private final Cache<K, V> delegate;
    
    public SynchronizedCache(Cache<K, V> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public synchronized void put(K key, V val) {
        this.delegate.put(key, val);
    }
    
    @Override
    public synchronized V get(K key) {
        return this.delegate.get(key);
    }
    
    @Override
    public V get(K key, Callable<? extends V> call) throws Exception {
        return this.delegate.get(key, call);
    }
    
    @Override
    public synchronized V remove(K key) {
        return this.delegate.remove(key);
    }
    
    @Override
    public synchronized void clear() {
        this.delegate.clear();
    }
    
    @Override
    public synchronized int getSize() {
        return this.delegate.getSize();
    }
}
