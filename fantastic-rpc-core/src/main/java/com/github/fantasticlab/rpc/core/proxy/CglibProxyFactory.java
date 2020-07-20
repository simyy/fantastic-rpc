package com.github.fantasticlab.rpc.core.proxy;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.example.HelloService;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


public class CglibProxyFactory extends AbstractProxyFactory {

    public CglibProxyFactory(String zk, String group) throws FrpcZookeeperException, InterruptedException {
        super(zk, group);
    }

    @Override
    public <T> T getProxy(Class<T> clz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return invocationHandler.invoke(obj, method, args);
            }
        });
        return (T) enhancer.create();
    }

}
