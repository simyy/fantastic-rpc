package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.RpcServer;
import com.github.fantasticlab.rpc.core.example.HelloService;
import com.github.fantasticlab.rpc.core.example.HelloServiceImpl;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.initializer.ConsumerInitializer;
import com.github.fantasticlab.rpc.core.initializer.ProviderInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InitializerTest {

    private String zk = "localhost:2181";
    private Integer port = 8080;
    private String group = "test";

    @Test
    public void test() throws FrpcZookeeperException, InterruptedException {

        ProviderInitializer providerInitializer = new ProviderInitializer(zk, port, group);
        providerInitializer.register(HelloServiceImpl.class);

        ConsumerInitializer consumerInitializer = new ConsumerInitializer(zk, group);
        HelloService helloService = consumerInitializer.getService(HelloService.class);

        Assert.assertTrue("Hi!!! Jack".equals(helloService.sayHi("Jack")));

    }

}
