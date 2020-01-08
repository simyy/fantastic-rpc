package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.net.NettyServer;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistry;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistryImpl;
import com.github.fantasticlab.rpc.core.registry.ProviderRegistry;
import com.github.fantasticlab.rpc.core.registry.ZookeeperProviderRegistry;
import com.github.fantasticlab.rpc.core.test.HelloServiceImpl;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 */
@Slf4j
public class RpcServer {

    private Integer port;

    private String group;

    //localhost:2181
    private String zk;

    private NettyServer server;

    private ZookeeperClient zookeeperClient;

    private ProviderRegistry providerRegistry;

    private ServiceRegistry serviceRegistry;


    // "localhost:2181"
    public RpcServer(String zk, Integer port, String group) throws FrpcZookeeperException {
        this.port = port;
        this.group = group;
        this.zk = zk;

        // init zookeeper
        this.zookeeperClient = new ZookeeperClientImpl(zk);
        // init provider registry
        this.providerRegistry = new ZookeeperProviderRegistry(zookeeperClient);
        // init service registry
        this.serviceRegistry = new ServiceRegistryImpl(group, this.port, providerRegistry);
        // init rpc server
        this.server = new NettyServer(port, serviceRegistry);
        try {
            server.start();
        } catch (InterruptedException e) {
            String errMsg = "RpcServer start failed";
            log.error(errMsg, e);
            throw new FrpcZookeeperException(errMsg, e);
        }
        log.info("RpcServer starting ...");
    }

    public void register(Class<?> clazz) {
        this.serviceRegistry.register(clazz);
    }

    public static void main(String[] args) throws FrpcZookeeperException {

        log.info("RpcServerTest start");

        String zk = "localhost:2181";
        Integer port = 8080;
        String group = "test";

        RpcServer rpcServer = new RpcServer(zk, port, group);
        rpcServer.register(HelloServiceImpl.class);

        // ProviderInitializer providerBean = new ProviderInitializer(HelloServiceImpl.class, rpcServer);
    }
}
