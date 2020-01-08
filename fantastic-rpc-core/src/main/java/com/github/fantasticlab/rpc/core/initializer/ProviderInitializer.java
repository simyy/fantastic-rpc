package com.github.fantasticlab.rpc.core.initializer;

import com.github.fantasticlab.rpc.core.RpcServer;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.test.HelloServiceImpl;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;


public class ProviderInitializer {

    private String zk;

    private Integer port;

    private String group;

    private RpcServer rpcServer;

    public ProviderInitializer(String zk, Integer port, String group) throws FrpcZookeeperException {
        this.zk = zk;
        this.port = port;
        this.group = group;
        this.rpcServer = new RpcServer(zk, port, group);
    }

    public <T> void register(Class<T> clazz) {
        this.rpcServer.register(clazz);
    }

    public static void main(String[] args) throws FrpcZookeeperException {

        String zk = "localhost:2181";
        Integer port = 8080;
        String group = "test";


        ProviderInitializer initializer = new ProviderInitializer(zk, port, group);
        initializer.register(HelloServiceImpl.class);
    }
}
