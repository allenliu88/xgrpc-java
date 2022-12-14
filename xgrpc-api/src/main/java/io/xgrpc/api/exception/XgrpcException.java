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

package io.xgrpc.api.exception;

import io.xgrpc.api.common.Constants;
import org.apache.commons.lang3.StringUtils;

/**
 * Xgrpc Exception.
 *
 * @author Xgrpc
 */
public class XgrpcException extends Exception {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -3913902031489277776L;

    private int errCode;

    private String errMsg;

    private Throwable causeThrowable;

    public XgrpcException() {
    }

    public XgrpcException(final int errCode, final String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public XgrpcException(final int errCode, final Throwable throwable) {
        super(throwable);
        this.errCode = errCode;
        this.setCauseThrowable(throwable);
    }

    public XgrpcException(final int errCode, final String errMsg, final Throwable throwable) {
        super(errMsg, throwable);
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.setCauseThrowable(throwable);
    }
    
    public int getErrCode() {
        return this.errCode;
    }
    
    public String getErrMsg() {
        if (!StringUtils.isBlank(this.errMsg)) {
            return this.errMsg;
        }
        if (this.causeThrowable != null) {
            return this.causeThrowable.getMessage();
        }
        return Constants.NULL;
    }
    
    public void setErrCode(final int errCode) {
        this.errCode = errCode;
    }
    
    public void setErrMsg(final String errMsg) {
        this.errMsg = errMsg;
    }
    
    public void setCauseThrowable(final Throwable throwable) {
        this.causeThrowable = this.getCauseThrowable(throwable);
    }
    
    private Throwable getCauseThrowable(final Throwable t) {
        if (t.getCause() == null) {
            return t;
        }
        return this.getCauseThrowable(t.getCause());
    }
    
    @Override
    public String toString() {
        return "ErrCode:" + getErrCode() + ", ErrMsg:" + getErrMsg();
    }
    
    /*
     * client error code.
     * -400 -503 throw exception to user.
     */
    
    /**
     * invalid param??????????????????.
     */
    public static final int CLIENT_INVALID_PARAM = -400;
    
    /**
     * client disconnect.
     */
    public static final int CLIENT_DISCONNECT = -401;
    
    /**
     * over client threshold?????????client?????????????????????.
     */
    public static final int CLIENT_OVER_THRESHOLD = -503;
    
    /*
     * server error code.
     * 400 403 throw exception to user
     * 500 502 503 change ip and retry
     */
    
    /**
     * invalid param??????????????????.
     */
    public static final int INVALID_PARAM = 400;
    
    /**
     * no right??????????????????.
     */
    public static final int NO_RIGHT = 403;
    
    /**
     * not found.
     */
    public static final int NOT_FOUND = 404;
    
    /**
     * conflict?????????????????????.
     */
    public static final int CONFLICT = 409;
    
    /**
     * server error???server?????????????????????.
     */
    public static final int SERVER_ERROR = 500;
    
    /**
     * client error???client??????????????????????????????.
     */
    public static final int CLIENT_ERROR = -500;
    
    /**
     * bad gateway?????????????????????nginx?????????Server?????????.
     */
    public static final int BAD_GATEWAY = 502;
    
    /**
     * over threshold?????????server?????????????????????.
     */
    public static final int OVER_THRESHOLD = 503;
    
    /**
     * Server is not started.
     */
    public static final int INVALID_SERVER_STATUS = 300;
    
    /**
     * Connection is not registered.
     */
    public static final int UN_REGISTER = 301;
    
    /**
     * No Handler Found.
     */
    public static final int NO_HANDLER = 302;
    
    public static final int RESOURCE_NOT_FOUND = -404;
    
    /**
     * http client error code, ome exceptions that occurred when the use the Xgrpc RestTemplate and Xgrpc
     * AsyncRestTemplate.
     */
    public static final int HTTP_CLIENT_ERROR_CODE = -500;
    
    
}
