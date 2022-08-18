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

package io.xgrpc.api.exception.runtime;

import static io.xgrpc.api.common.Constants.Exception.SERIALIZE_ERROR_CODE;

/**
 * Xgrpc serialization exception.
 *
 * @author yangyi
 */
public class XgrpcSerializationException extends XgrpcRuntimeException {
    
    private static final long serialVersionUID = -4308536346316915612L;
    
    private static final String DEFAULT_MSG = "Xgrpc serialize failed. ";
    
    private static final String MSG_FOR_SPECIFIED_CLASS = "Xgrpc serialize for class [%s] failed. ";
    
    private Class<?> serializedClass;
    
    public XgrpcSerializationException() {
        super(SERIALIZE_ERROR_CODE);
    }
    
    public XgrpcSerializationException(Class<?> serializedClass) {
        super(SERIALIZE_ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()));
        this.serializedClass = serializedClass;
    }
    
    public XgrpcSerializationException(Throwable throwable) {
        super(SERIALIZE_ERROR_CODE, DEFAULT_MSG, throwable);
    }
    
    public XgrpcSerializationException(Class<?> serializedClass, Throwable throwable) {
        super(SERIALIZE_ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()), throwable);
        this.serializedClass = serializedClass;
    }
    
    public Class<?> getSerializedClass() {
        return serializedClass;
    }
}
