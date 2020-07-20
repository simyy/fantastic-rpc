package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.RpcServer;
import com.github.fantasticlab.rpc.core.example.HelloService;
import com.github.fantasticlab.rpc.core.example.HelloServiceImpl;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.proxy.JdkProxyFactory;
import com.github.fantasticlab.rpc.core.proxy.ProxyFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProxyFactoryTest {

    private String zk = "localhost:2181";
    private String group = "providerInitTest";

    @Before
    public void setUp() throws Exception {
        Integer port = 8080;

        RpcServer rpcServer = new RpcServer(zk, port, group);
        rpcServer.register(HelloServiceImpl.class);
    }

    @Test
    public void jdkProxyTest() throws FrpcZookeeperException, InterruptedException {
        ProxyFactory proxy = new JdkProxyFactory(zk, group);
        HelloService helloService = proxy.getProxy(HelloService.class);

        Assert.assertEquals(helloService.sayHi(), "Hi!!!");
        Assert.assertEquals(helloService.sayHi("George"), "Hi!!! George");
    }

    @Test
    public void cglibProxyTest() throws FrpcZookeeperException, InterruptedException {
        ProxyFactory proxy = new JdkProxyFactory(zk, group);
        HelloService helloService = proxy.getProxy(HelloService.class);

        Assert.assertEquals(helloService.sayHi(), "Hi!!!");
        Assert.assertEquals(helloService.sayHi("George"), "Hi!!! George");
    }
}
