package com.github.fantasticlab.rpc.core.initializer;

import com.github.fantasticlab.rpc.core.RpcServer;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.example.HelloServiceImpl;


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

}
