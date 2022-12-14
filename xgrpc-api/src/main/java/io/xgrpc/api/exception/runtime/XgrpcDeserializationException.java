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

package io.xgrpc.api.exception.runtime;

import static io.xgrpc.api.common.Constants.Exception.DESERIALIZE_ERROR_CODE;

import java.lang.reflect.Type;

/**
 * Xgrpc deserialization exception.
 *
 * @author yangyi
 */
public class XgrpcDeserializationException extends XgrpcRuntimeException {
    
    private static final long serialVersionUID = -2742350751684273728L;
    
    private static final String DEFAULT_MSG = "Xgrpc deserialize failed. ";
    
    private static final String MSG_FOR_SPECIFIED_CLASS = "Xgrpc deserialize for class [%s] failed. ";
    
    private static final String ERROR_MSG_FOR_SPECIFIED_CLASS = "Xgrpc deserialize for class [%s] failed, cause error[%s]. ";
    
    private Class<?> targetClass;
    
    public XgrpcDeserializationException() {
        super(DESERIALIZE_ERROR_CODE);
    }
    
    public XgrpcDeserializationException(Class<?> targetClass) {
        super(DESERIALIZE_ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }
    
    public XgrpcDeserializationException(Type targetType) {
        super(DESERIALIZE_ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()));
    }
    
    public XgrpcDeserializationException(Throwable throwable) {
        super(DESERIALIZE_ERROR_CODE, DEFAULT_MSG, throwable);
    }
    
    public XgrpcDeserializationException(Class<?> targetClass, Throwable throwable) {
        super(DESERIALIZE_ERROR_CODE, String.format(ERROR_MSG_FOR_SPECIFIED_CLASS, targetClass.getName(), throwable.getMessage()), throwable);
        this.targetClass = targetClass;
    }
    
    public XgrpcDeserializationException(Type targetType, Throwable throwable) {
        super(DESERIALIZE_ERROR_CODE, String.format(ERROR_MSG_FOR_SPECIFIED_CLASS, targetType.toString(), throwable.getMessage()), throwable);
    }
    
    public Class<?> getTargetClass() {
        return targetClass;
    }
}
