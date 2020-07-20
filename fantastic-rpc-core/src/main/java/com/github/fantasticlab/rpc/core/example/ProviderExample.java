package com.github.fantasticlab.rpc.core.example;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.initializer.ProviderInitializer;

public class ProviderExample {

    public static void main(String[] args) throws FrpcZookeeperException {

        String zk = "localhost:2181";
        Integer port = 8080;
        String group = "test";

        ProviderInitializer initializer = new ProviderInitializer(zk, port, group);
        initializer.register(HelloServiceImpl.class);
    }
}
