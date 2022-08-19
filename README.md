## Introduction

Build an excellent grpc-java framework, inspired by [Nacos](https://nacos.io/zh-cn/index.html).

## Build and Install

```shell
mvn clean install -Dmaven.test.skip=true
```

## Usage

[Usage Example Git Repo](https://github.com/allenliu88/xgrpc-java-example).
- [Server](https://github.com/allenliu88/xgrpc-java-example/tree/main/animal-name-service)
- [Client](https://github.com/allenliu88/xgrpc-java-example/tree/main/name-generator-service)

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