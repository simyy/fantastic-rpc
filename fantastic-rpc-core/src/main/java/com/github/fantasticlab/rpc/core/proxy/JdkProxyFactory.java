package com.github.fantasticlab.rpc.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * JDK代理实现
 */
public class JdkProxyFactory implements ProxyFactory {

    public <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clz}, invocationHandler);
    }

}
