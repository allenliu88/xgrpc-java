/*
 *  Copyright 1999-2021 Xgrpc Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.xgrpc.core.remote.grpc;

import static io.xgrpc.core.remote.grpc.BaseGrpcServer.CONTEXT_KEY_CONN_ID;
import static io.xgrpc.core.remote.grpc.BaseGrpcServer.CONTEXT_KEY_CONN_LOCAL_PORT;
import static io.xgrpc.core.remote.grpc.BaseGrpcServer.CONTEXT_KEY_CONN_REMOTE_IP;
import static io.xgrpc.core.remote.grpc.BaseGrpcServer.CONTEXT_KEY_CONN_REMOTE_PORT;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.grpc.auto.Payload;
import io.xgrpc.api.grpc.auto.RequestGrpc;
import io.xgrpc.api.remote.request.HealthCheckRequest;
import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.request.ServerCheckRequest;
import io.xgrpc.api.remote.response.ErrorResponse;
import io.xgrpc.api.remote.response.HealthCheckResponse;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.api.remote.response.ServerCheckResponse;
import io.xgrpc.common.remote.PayloadRegistry;
import io.xgrpc.common.remote.client.grpc.GrpcUtils;
import io.xgrpc.core.remote.connection.Connection;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.core.remote.connection.ConnectionMeta;
import io.xgrpc.core.remote.handler.RequestHandler;
import io.xgrpc.core.remote.registry.RequestHandlerRegistry;
import io.xgrpc.sys.utils.ApplicationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * {@link GrpcRequestAcceptor} unit test.
 *
 * @author chenglu
 * @date 2021-07-01 10:49
 */
@RunWith(MockitoJUnitRunner.class)
public class GrpcRequestAcceptorTest {
    
    @Rule
    public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();
    
    @Mock
    private ConnectionManager connectionManager;
    
    @Mock
    private RequestHandlerRegistry requestHandlerRegistry;
    
    @InjectMocks
    private GrpcRequestAcceptor acceptor;
    
    private RequestGrpc.RequestStub streamStub;
    
    private String connectId = UUID.randomUUID().toString();
    
    private String requestId = UUID.randomUUID().toString();
    
    private MockRequestHandler mockHandler;
    
    @Before
    public void setUp() throws IOException {
        String serverName = InProcessServerBuilder.generateName();
        String remoteIp = "127.0.0.1";
        grpcCleanupRule.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(acceptor)
                .intercept(new ServerInterceptor() {
                    @Override
                    public <R, S> ServerCall.Listener<R> interceptCall(ServerCall<R, S> serverCall, Metadata metadata,
                            ServerCallHandler<R, S> serverCallHandler) {
                        Context ctx = Context.current().withValue(CONTEXT_KEY_CONN_ID, UUID.randomUUID().toString())
                                .withValue(CONTEXT_KEY_CONN_LOCAL_PORT, 1234)
                                .withValue(CONTEXT_KEY_CONN_REMOTE_PORT, 8948)
                                .withValue(CONTEXT_KEY_CONN_REMOTE_IP, remoteIp);
                        return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
                    }
                }).build().start());
        streamStub = RequestGrpc.newStub(
                grpcCleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
        mockHandler = new MockRequestHandler();
        PayloadRegistry.init();
    }
    
    @Test
    public void testApplicationUnStarted() {
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);
        ServerCheckRequest serverCheckRequest = new ServerCheckRequest();
        serverCheckRequest.setRequestId(requestId);
        Payload request = GrpcUtils.convert(serverCheckRequest, metadata);
        
        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                Assert.assertTrue(res instanceof ErrorResponse);
                ErrorResponse errorResponse = (ErrorResponse) res;
                Assert.assertEquals(errorResponse.getErrorCode(), XgrpcException.INVALID_SERVER_STATUS);
            }
            
            @Override
            public void onError(Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };
        
        streamStub.request(request, streamObserver);
    }
    
    @Test
    public void testServerCheckRequest() {
        ApplicationUtils.setStarted(true);
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);
        ServerCheckRequest serverCheckRequest = new ServerCheckRequest();
        serverCheckRequest.setRequestId(requestId);
        Payload request = GrpcUtils.convert(serverCheckRequest, metadata);
        
        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                Assert.assertTrue(res instanceof ServerCheckResponse);
            }
            
            @Override
            public void onError(Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };
        
        streamStub.request(request, streamObserver);
        ApplicationUtils.setStarted(false);
    }
    
    @Test
    public void testNoRequestHandler() {
        ApplicationUtils.setStarted(true);
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);
        InstanceRequest instanceRequest = new InstanceRequest();
        instanceRequest.setRequestId(requestId);
        Payload request = GrpcUtils.convert(instanceRequest, metadata);
        
        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                Assert.assertTrue(res instanceof ErrorResponse);
                
                ErrorResponse errorResponse = (ErrorResponse) res;
                Assert.assertEquals(errorResponse.getErrorCode(), XgrpcException.NO_HANDLER);
            }
            
            @Override
            public void onError(Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };
        
        streamStub.request(request, streamObserver);
        ApplicationUtils.setStarted(false);
    }
    
    @Test
    public void testConnectionNotRegister() {
        ApplicationUtils.setStarted(true);
        Mockito.when(requestHandlerRegistry.getByRequestType(Mockito.anyString())).thenReturn(mockHandler);
        Mockito.when(connectionManager.checkValid(Mockito.any())).thenReturn(false);
        
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);
        InstanceRequest instanceRequest = new InstanceRequest();
        instanceRequest.setRequestId(requestId);
        Payload request = GrpcUtils.convert(instanceRequest, metadata);
        
        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                Assert.assertTrue(res instanceof ErrorResponse);
                
                ErrorResponse errorResponse = (ErrorResponse) res;
                Assert.assertEquals(errorResponse.getErrorCode(), XgrpcException.UN_REGISTER);
            }
            
            @Override
            public void onError(Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };
        
        streamStub.request(request, streamObserver);
        ApplicationUtils.setStarted(false);
    }
    
    @Test
    public void testRequestContentError() {
        ApplicationUtils.setStarted(true);
        Mockito.when(requestHandlerRegistry.getByRequestType(Mockito.anyString())).thenReturn(mockHandler);
        Mockito.when(connectionManager.checkValid(Mockito.any())).thenReturn(true);
        
        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                Assert.assertTrue(res instanceof ErrorResponse);
                
                ErrorResponse errorResponse = (ErrorResponse) res;
                Assert.assertEquals(errorResponse.getErrorCode(), XgrpcException.BAD_GATEWAY);
            }
            
            @Override
            public void onError(Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };
        
        streamStub.request(null, streamObserver);
        ApplicationUtils.setStarted(false);
    }
    
    @Test
    public void testHandleRequestSuccess() {
        ApplicationUtils.setStarted(true);
        Mockito.when(requestHandlerRegistry.getByRequestType(Mockito.anyString())).thenReturn(mockHandler);
        Mockito.when(connectionManager.checkValid(Mockito.any())).thenReturn(true);
        String ip = "1.1.1.1";
        ConnectionMeta connectionMeta = new ConnectionMeta(connectId, ip, ip, 8888, 9848, "GRPC", "", "",
                new HashMap<>());
        Connection connection = new GrpcConnection(connectionMeta, null, null);
        Mockito.when(connectionManager.getConnection(Mockito.any())).thenReturn(connection);
        
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);
        HealthCheckRequest mockRequest = new HealthCheckRequest();
        Payload payload = GrpcUtils.convert(mockRequest, metadata);
        
        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                Assert.assertTrue(res instanceof HealthCheckResponse);
            }
            
            @Override
            public void onError(Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };
        
        streamStub.request(payload, streamObserver);
        ApplicationUtils.setStarted(false);
    }
    
    @Test
    public void testHandleRequestError() {
        ApplicationUtils.setStarted(true);
        Mockito.when(requestHandlerRegistry.getByRequestType(Mockito.anyString())).thenReturn(mockHandler);
        Mockito.when(connectionManager.checkValid(Mockito.any())).thenReturn(true);
        
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);
        InstanceRequest instanceRequest = new InstanceRequest();
        Payload payload = GrpcUtils.convert(instanceRequest, metadata);
        
        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                Assert.assertTrue(res instanceof ErrorResponse);
                
                ErrorResponse errorResponse = (ErrorResponse) res;
                Assert.assertEquals(errorResponse.getErrorCode(), XgrpcException.SERVER_ERROR);
            }
            
            @Override
            public void onError(Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };
        
        streamStub.request(payload, streamObserver);
        ApplicationUtils.setStarted(false);
    }
    
    /**
     * add this Handler just for test.
     */
    class MockRequestHandler extends RequestHandler<HealthCheckRequest, HealthCheckResponse> {
        
        @Override
        public Response handleRequest(HealthCheckRequest request, RequestMeta meta) throws XgrpcException {
            return handle(request, meta);
        }
        
        @Override
        public HealthCheckResponse handle(HealthCheckRequest request, RequestMeta meta) throws XgrpcException {
            System.out.println("MockHandler get request: " + request + " meta: " + meta);
            return new HealthCheckResponse();
        }
    }

    class InstanceRequest extends Request {

        @Override
        public String getModule() {
            return "test-module";
        }
    }
}
