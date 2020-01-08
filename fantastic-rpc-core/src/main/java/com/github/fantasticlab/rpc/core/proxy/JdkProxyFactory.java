package com.github.fantasticlab.rpc.core.proxy;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.test.HelloService;
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

    public static void main(String[] args) throws FrpcZookeeperException, InterruptedException {

        String zk = "localhost:2181";
        String group = "test";
        ProxyFactory proxy = new JdkProxyFactory(zk, group);
        HelloService helloService = proxy.getProxy(HelloService.class);


        while (true) {

            Thread.sleep(2000);

            try {

                System.out.println(helloService.sayHi());
                System.out.println(helloService.sayHi("George"));
            } catch (Exception e) {
                log.error("exception 111", e);
            }
        }
    }

}
