/*
 * Copyright 1999-2020 Xgrpc Holding Ltd.
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

package io.xgrpc.api.remote.response;

import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.exception.runtime.XgrpcRuntimeException;

/**
 * UnKnowResponse.
 *
 * @author liuzunfei
 * @version $Id: UnKnowResponse.java, v 0.1 2020年07月16日 9:47 PM liuzunfei Exp $
 */
public class ErrorResponse extends Response {
    
    /**
     * build an error response.
     *
     * @param errorCode errorCode
     * @param msg msg
     * @return response
     */
    public static Response build(int errorCode, String msg) {
        ErrorResponse response = new ErrorResponse();
        response.setErrorInfo(errorCode, msg);
        return response;
    }
    
    /**
     * build an error response.
     *
     * @param exception exception
     * @return response
     */
    public static Response build(Throwable exception) {
        int errorCode;
        if (exception instanceof XgrpcException) {
            errorCode = ((XgrpcException) exception).getErrCode();
        } else if (exception instanceof XgrpcRuntimeException) {
            errorCode = ((XgrpcRuntimeException) exception).getErrCode();
        } else {
            errorCode = ResponseCode.FAIL.getCode();
        }
        ErrorResponse response = new ErrorResponse();
        response.setErrorInfo(errorCode, exception.getMessage());
        return response;
    }
    
}
