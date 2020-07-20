package com.github.fantasticlab.rpc.core.example;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.initializer.ConsumerInitializer;

public class ConsumerExample {

    public static void main(String[] args) throws FrpcZookeeperException, InterruptedException {

        String zk = "localhost:2181";
        String group = "test";

        ConsumerInitializer initializer = new ConsumerInitializer(zk, group);
        HelloService helloService = initializer.getService(HelloService.class);
        helloService.sayHi("Hello!!!");

    }
}
