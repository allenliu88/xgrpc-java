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

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.xgrpc.api.PropertyKeyConst;
import io.xgrpc.api.exception.XgrpcException;

/**
 * All parameter validation tools.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class ValidatorUtils {
    
    private static final Pattern CONTEXT_PATH_MATCH = Pattern.compile("(\\/)\\1+");
    
    public static void checkInitParam(Properties properties) throws XgrpcException {
        checkContextPath(properties.getProperty(PropertyKeyConst.CONTEXT_PATH));
    }
    
    /**
     * Check context path.
     *
     * @param contextPath context path
     */
    public static void checkContextPath(String contextPath) {
        if (contextPath == null) {
            return;
        }
        Matcher matcher = CONTEXT_PATH_MATCH.matcher(contextPath);
        if (matcher.find()) {
            throw new IllegalArgumentException("Illegal url path expression");
        }
    }
    
}
