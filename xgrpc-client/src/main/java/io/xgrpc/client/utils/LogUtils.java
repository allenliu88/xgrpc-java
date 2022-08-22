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

package io.xgrpc.client.utils;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

/**
 * Log utils.
 *
 * @author <a href="mailto:huangxiaoyu1018@gmail.com">hxy1991</a>
 * @since 0.9.0
 */
public class LogUtils {
    
    public static final Logger NAMING_LOGGER;
    
    static {
        NAMING_LOGGER = getLogger("io.xgrpc.client.naming");
    }
    
    public static Logger logger(Class<?> clazz) {
        return getLogger(clazz);
    }
    
}
