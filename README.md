## Introduction

Build an excellent grpc-java framework, inspired by [Nacos](https://nacos.io/zh-cn/index.html).

## Build and Install

```shell
mvn clean install -Dmaven.test.skip=true
```

## Repo
- [Java Server](https://github.com/allenliu88/xgrpc-java/tree/main/xgrpc-core)
- [Java Client](https://github.com/allenliu88/xgrpc-java/tree/main/xgrpc-client)
- [Golang Client](https://github.com/allenliu88/xgrpc-client-go)

## Usage

[Usage Example Git Repo](https://github.com/allenliu88/xgrpc-java-example).
- [Server](https://github.com/allenliu88/xgrpc-java-example/tree/main/animal-name-service)
- [Client](https://github.com/allenliu88/xgrpc-java-example/tree/main/name-generator-service)

## From client to server

### Request and Response in client and server

- Request extends `io.xgrpc.api.remote.request.Request`
- Response extends `io.xgrpc.api.remote.request.Response`
- Add the request and response packages to custom provider

```java
package com.example.remote;

import java.util.HashSet;
import java.util.Set;

import com.google.auto.service.AutoService;
import io.xgrpc.common.remote.PayloadPackageProvider;

/**
 * client package provider.
 *
 * @author hujun
 */
@AutoService(PayloadPackageProvider.class)
public class ClientPayloadPackageProvider implements PayloadPackageProvider {
    
    private final Set<String> scanPackage = new HashSet<>();
    
    {
        scanPackage.add("com.example.dto");
    }
    
    @Override
    public Set<String> getScanPackage() {
        return scanPackage;
    }
}
```

### RPC Server init
pom.xml:
```xml
	<dependencies>
	    <dependency>
            <groupId>io.xgrpc</groupId>
            <artifactId>xgrpc-core</artifactId>
            <version>0.1.0-SNAPSHOT</version>
        </dependency>
        <!-- SPI autoservice -->
        <dependency>
            <groupId>com.google.auto.service</groupId>
            <artifactId>auto-service</artifactId>
            <version>1.0.1</version>
            <optional>true</optional>
        </dependency>
    </dependencies>
```

Server init:
```java
@SpringBootApplication
@EnableFeignClients
public class AnimalNameService {

    public static void main(String[] args) {
        GuiceInjectorBootstrap.getBean(BaseGrpcServer.class);
        SpringApplication.run(AnimalNameService.class, args);
    }

}
```
> `GuiceInjectorBootstrap.getBean(BaseGrpcServer.class);` will start server automatically.

Handler:
```java
package com.example.handler;

import com.example.dto.DemoRequest;
import com.example.dto.DemoResponse;
import com.google.auto.service.AutoService;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.core.remote.handler.RequestHandler;

@AutoService(RequestHandler.class)
public class DemoRequestHandler extends RequestHandler<DemoRequest, DemoResponse> {
    @Override
    public DemoResponse handle(DemoRequest request, RequestMeta meta) throws XgrpcException {
        return new DemoResponse().setMsg("hello world.");
    }
}
```

### RPC Client init
pom.xml:
```xml
    <dependencies>
        <dependency>
            <groupId>io.xgrpc</groupId>
            <artifactId>xgrpc-client</artifactId>
            <version>0.1.0-SNAPSHOT</version>
        </dependency>
        <!-- SPI autoservice -->
        <dependency>
            <groupId>com.google.auto.service</groupId>
            <artifactId>auto-service</artifactId>
            <version>1.0.1</version>
            <optional>true</optional>
        </dependency>
    </dependencies>
```

Uni-Request:
```java
public class DemoApp {
    public void executeDemoRequest0() {
        RpcClientManager rpcClientManager =
                new DefaultRpcClientManager(ConnectionType.GRPC, new ServerListManager(Arrays.asList("127.0.0.1:8848")));

        String connectId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);

        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setRequestId(requestId);
        Response demoResponse = rpcClientManager.request(rpcClientManager.build("0"), demoRequest, 5000);

        System.out.println("response type: " + (demoResponse instanceof  DemoResponse));
        System.out.println(((DemoResponse)demoResponse).getMsg());
    }
}
```
> Note: the server port `8848` will be mapping to remote rpc server `9848`.

### Test
```shell
curl -v http://127.0.0.1:8080/api/v1/names/random
```

## From server to client

### Request and Response in client and server
The same as from client to server.

- Request extends `io.xgrpc.api.remote.request.Request`
- Response extends `io.xgrpc.api.remote.request.Response`
- Add the request and response packages to custom provider

com.example.dto.DemoServerRequest
```java
package com.example.dto;

import io.xgrpc.api.remote.request.ServerRequest;

public class DemoServerRequest extends ServerRequest {
    private String name;

    public String getName() {
        return name;
    }

    public DemoServerRequest setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getModule() {
        return "server module";
    }
}
```

com.example.dto.DemoServerResponse
```java
package com.example.dto;

import io.xgrpc.api.remote.response.Response;

public class DemoServerResponse extends Response {
    String msg;

    public String getMsg() {
        return msg;
    }

    public DemoServerResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
```

### RPC Server push to client

sync request and async request:
> Note: must use `RpcPushService rpcPushService = GuiceInjectorBootstrap.getBean(RpcPushService.class);` to get `RpcPushService` instance from guice injector.
```java
public class AnimalNameResource {
    @GetMapping(path = "/push")
    public String push(@RequestHeader HttpHeaders headers) {
        String name = animalNames.get(random.nextInt(animalNames.size()));
        // String scientist = scientistServiceClient.randomScientistName();

        // name = toKebabCase(scientist) + "-" + toKebabCase(name);

        System.out.println("===========================================");
        System.out.println("HttpHeaders: " + headers);
        System.out.println("===========================================");
        // throw new RuntimeException("Invalid Operations.");


        // this.syncServerRequest();
        this.asyncServerRequest();
        return name;
    }

    /**
     * 服务器端向客户端发送同步请求
     */
    private void syncServerRequest() {
        RpcPushService rpcPushService = GuiceInjectorBootstrap.getBean(RpcPushService.class);
        DemoServerRequest demoServerRequest = new DemoServerRequest().setName("AnimalNameService");
        Map<String, Response> ret = rpcPushService.pushWithoutAck(Collections.singletonMap("uuidName", "NameGeneratorService"), demoServerRequest);
        ret.forEach((key, value) -> System.out.println("========From client connection id [" + key + "], msg: " + ((DemoServerResponse)value).getMsg()));
    }

    /**
     * 服务器端向客户端发送异步请求
     */
    private void asyncServerRequest() {
        RpcPushService rpcPushService = GuiceInjectorBootstrap.getBean(RpcPushService.class);
        DemoServerRequest demoServerRequest = new DemoServerRequest().setName("AnimalNameService");
        rpcPushService.pushWithCallback(
                Collections.singletonMap("uuidName", "NameGeneratorService"),
                demoServerRequest,
                new PushCallBack() {
                    @Override
                    public long getTimeout() {
                        return 10000;
                    }

                    @Override
                    public void onSuccess(Response response) {
                        System.out.println("========From client async server request, msg: " + ((DemoServerResponse)response).getMsg());
                    }

                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }
                },
                null
        );
    }
}
```

### RPC client handle server request
Note the label and server request handler:
- label: the label uniquely identify the client connection by the server
- server request handler: use to handle the server request

```java
public class NameResource {
    @GetMapping(path = "/random")
    public String name(@RequestHeader HttpHeaders headers) throws Exception {
        String animal = animalServiceClient.randomAnimalName();
        String name = animal;
        // String scientist = scientistServiceClient.randomScientistName();
        // String name = toKebabCase(scientist) + "-" + toKebabCase(animal);
        System.out.println("===========================================");
        System.out.println("HttpHeaders: " + headers);
        System.out.println("===========================================");

        executeRequestAndHandleServerRequest();
        return name;
    }

    /**
     * 客户端请求服务器端：
     * 1. 初始建立连接，生成RPC客户端
     * 2. 注册服务器端请求处理器
     * 3. 发送请求
     */
    private void executeRequestAndHandleServerRequest() {
        RpcClientManager rpcClientManager =
                new DefaultRpcClientManager(ConnectionType.GRPC, new ServerListManager(Arrays.asList("127.0.0.1:8848")))
                        .addLabel("uuidName", "NameGeneratorService")
                        .addServerRequestHandler(new ServerRequestHandler() {
                            @Override
                            public Response requestReply(Request request) {
                                System.out.println("=======request class is " + request.getClass().getName());
                                if (request instanceof DemoServerRequest) {
                                    DemoServerRequest demoServerRequest = (DemoServerRequest) request;
                                    System.out.println("======server is " + demoServerRequest.getName());
                                }
                                return new DemoServerResponse().setMsg("hello, i'm client NameGeneratorService.");
                            }
                        });

        String connectId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);

        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setRequestId(requestId);
        Response demoResponse = rpcClientManager.request(rpcClientManager.build("0"), demoRequest, 5000);

        System.out.println("response type: " + (demoResponse instanceof  DemoResponse));
        System.out.println(((DemoResponse)demoResponse).getMsg());
    }
}
```

### Test
> Note: must first create the rpc client, register the connection to server, then the server can push back.

```shell
## Initialize the client, register the connection to server
curl -v http://127.0.0.1:8080/api/v1/names/random

## Use the registered connection to push message back to client
curl -v http://127.0.0.1:9000/api/v1/animals/push
```