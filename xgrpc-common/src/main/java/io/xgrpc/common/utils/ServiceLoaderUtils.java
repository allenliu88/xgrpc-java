package io.xgrpc.common.utils;

import java.util.List;
import java.util.ServiceLoader;

import com.google.common.collect.Lists;

public class ServiceLoaderUtils {
    private ServiceLoaderUtils() {
    }

    public static <T> List<T> load(Class<T> tClass) {
        return Lists.newArrayList(ServiceLoader.load(tClass).iterator());
    }
}
