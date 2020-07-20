package com.github.fantasticlab.rpc.core.proxy;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.example.HelloService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * JDK代理实现
 */
@Slf4j
public class JdkProxyFactory extends AbstractProxyFactory {

    public JdkProxyFactory(String zk, String group) throws FrpcZookeeperException, InterruptedException {
        super(zk, group);
    }

    @SuppressWarnings("all")
    public <T> T getProxy(Class<T> clz) {
        return (T) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{clz},
                invocationHandler);
    }
}
