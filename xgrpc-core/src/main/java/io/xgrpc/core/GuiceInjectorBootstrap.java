package io.xgrpc.core;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.xgrpc.core.remote.connection.ConnectionManager;
import io.xgrpc.core.remote.grpc.BaseGrpcServer;
import io.xgrpc.core.remote.grpc.GrpcBiStreamRequestAcceptor;
import io.xgrpc.core.remote.grpc.GrpcRequestAcceptor;
import io.xgrpc.core.remote.grpc.GrpcSdkServer;
import io.xgrpc.core.remote.push.RpcPushService;
import io.xgrpc.core.remote.registry.ClientConnectionEventListenerRegistry;
import io.xgrpc.core.remote.registry.RequestHandlerRegistry;

public class GuiceInjectorBootstrap {
    private static final Injector INSTANCE = Guice.createInjector(new MainModule());

    static class MainModule extends AbstractModule {
        @Override
        protected void configure() {
            super.configure();
            bind(BaseGrpcServer.class).to(GrpcSdkServer.class).in(Singleton.class);
            bind(GrpcBiStreamRequestAcceptor.class).in(Singleton.class);
            bind(GrpcRequestAcceptor.class).in(Singleton.class);
            bind(RequestHandlerRegistry.class).in(Singleton.class);
            bind(ClientConnectionEventListenerRegistry.class).in(Singleton.class);
            bind(ConnectionManager.class).in(Singleton.class);
            bind(RpcPushService.class).in(Singleton.class);
        }
    }

    /**
     * 获取实例
     *
     * @param tClass 类型信息
     * @return 类实例
     * @param <T> 类型信息
     */
    public static <T>  T getBean(Class<T> tClass) {
        return INSTANCE.getInstance(tClass);
    }
}
