package com.github.fantasticlab.rpc.core.proxy;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.test.HelloService;
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

    public static void main(String[] args) throws FrpcZookeeperException, InterruptedException {

        String zk = "localhost:2181";
        String group = "test";
        ProxyFactory proxy = new CglibProxyFactory(zk, group);
        HelloService helloService = proxy.getProxy(HelloService.class);

        Thread.sleep(2000);

        System.out.println(helloService.sayHi());
        System.out.println(helloService.sayHi("George"));
    }
}
