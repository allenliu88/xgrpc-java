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

package io.xgrpc.common.remote.client.grpc;

import java.nio.ByteBuffer;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.UnsafeByteOperations;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.grpc.auto.Metadata;
import io.xgrpc.api.grpc.auto.Payload;
import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.api.utils.NetUtils;
import io.xgrpc.common.remote.PayloadRegistry;
import io.xgrpc.common.remote.exception.RemoteException;
import io.xgrpc.common.utils.JacksonUtils;

/**
 * gRPC utils, use to parse request and response.
 *
 * @author liuzunfei
 * @version $Id: GrpcUtils.java, v 0.1 2020年08月09日 1:43 PM liuzunfei Exp $
 */
public class GrpcUtils {
    
    /**
     * convert request to payload.
     *
     * @param request request.
     * @param meta    request meta.
     * @return payload.
     */
    public static Payload convert(Request request, RequestMeta meta) {
        //meta.
        Payload.Builder payloadBuilder = Payload.newBuilder();
        Metadata.Builder metaBuilder = Metadata.newBuilder();
        if (meta != null) {
            metaBuilder.putAllHeaders(request.getHeaders()).setType(request.getClass().getSimpleName());
        }
        metaBuilder.setClientIp(NetUtils.localIP());
        payloadBuilder.setMetadata(metaBuilder.build());
        
        // request body .
        request.clearHeaders();
        byte[] jsonBytes = JacksonUtils.toJsonBytes(request);
        return payloadBuilder
                .setBody(Any.newBuilder().setValue(UnsafeByteOperations.unsafeWrap(jsonBytes)))
                .build();
        
    }
    
    /**
     * convert request to payload.
     *
     * @param request request.
     * @return payload.
     */
    public static Payload convert(Request request) {
        
        Metadata newMeta = Metadata.newBuilder().setType(request.getClass().getSimpleName())
                .setClientIp(NetUtils.localIP()).putAllHeaders(request.getHeaders()).build();
        request.clearHeaders();
        byte[] jsonBytes = JacksonUtils.toJsonBytes(request);
        
        Payload.Builder builder = Payload.newBuilder();
    
        return builder
                .setBody(Any.newBuilder().setValue(UnsafeByteOperations.unsafeWrap(jsonBytes)))
                .setMetadata(newMeta).build();
        
    }
    
    /**
     * convert response to payload.
     *
     * @param response response.
     * @return payload.
     */
    public static Payload convert(Response response) {
        byte[] jsonBytes = JacksonUtils.toJsonBytes(response);
        
        Metadata.Builder metaBuilder = Metadata.newBuilder().setType(response.getClass().getSimpleName());
        return Payload.newBuilder()
                .setBody(Any.newBuilder().setValue(UnsafeByteOperations.unsafeWrap(jsonBytes)))
                .setMetadata(metaBuilder.build()).build();
    }
    
    /**
     * parse payload to request/response model.
     *
     * @param payload payload to be parsed.
     * @return payload
     */
    public static Object parse(Payload payload) {
        Class classType = PayloadRegistry.getClassByType(payload.getMetadata().getType());
        if (classType != null) {
            ByteString byteString = payload.getBody().getValue();
            ByteBuffer byteBuffer = byteString.asReadOnlyByteBuffer();
            Object obj = JacksonUtils.toObj(new ByteBufferBackedInputStream(byteBuffer), classType);
            if (obj instanceof Request) {
                ((Request) obj).putAllHeader(payload.getMetadata().getHeadersMap());
            }
            return obj;
        } else {
            throw new RemoteException(XgrpcException.SERVER_ERROR,
                    "Unknown payload type:" + payload.getMetadata().getType());
        }
        
    }
    
    public static class PlainRequest {
        
        String type;
        
        Object body;
        
        /**
         * Getter method for property <tt>type</tt>.
         *
         * @return property value of type
         */
        public String getType() {
            return type;
        }
        
        /**
         * Setter method for property <tt>type</tt>.
         *
         * @param type value to be assigned to property type
         */
        public void setType(String type) {
            this.type = type;
        }
        
        /**
         * Getter method for property <tt>body</tt>.
         *
         * @return property value of body
         */
        public Object getBody() {
            return body;
        }
        
        /**
         * Setter method for property <tt>body</tt>.
         *
         * @param body value to be assigned to property body
         */
        public void setBody(Object body) {
            this.body = body;
        }
    }
    
}
