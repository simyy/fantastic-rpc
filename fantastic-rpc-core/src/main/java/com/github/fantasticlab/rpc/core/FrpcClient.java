package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.discovery.ZKServiceDiscovery;
import com.github.fantasticlab.rpc.core.exception.RegistryException;
import com.github.fantasticlab.rpc.core.meta.ConsumerNode;
import com.github.fantasticlab.rpc.core.meta.PathUtil;
import com.github.fantasticlab.rpc.core.proxy.JdkProxyFactory;
import com.github.fantasticlab.rpc.core.proxy.ProxyFactory;
import com.github.fantasticlab.rpc.core.registry.ConsumerRegistry;
import com.github.fantasticlab.rpc.core.registry.ZKConsumerRegistry;
import com.github.fantasticlab.rpc.core.util.NetUtils;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;

public class FrpcClient {

    private static ProxyFactory proxyFactory = new JdkProxyFactory();

    private String service;

    private String group;

    private Discovery discovery;

    public FrpcClient(String service, String group) {
        this.service = service;
        this.group = group;
    }

    public synchronized void init() throws RegistryException {

        ConsumerNode node = new ConsumerNode();
        node.setService(service);
        node.setGroup(group);
        node.setNodeKey(PathUtil.buildConsumerPath(service, group));
        node.setAddress(NetUtils.getLocalIp());

        ZkClient zkClient = new ZkClientImpl();
        // register
        ConsumerRegistry consumerRegistry = new ZKConsumerRegistry(zkClient);
        consumerRegistry.register(node);
        // discovery
        discovery = new ZKServiceDiscovery(zkClient);
    }

}
