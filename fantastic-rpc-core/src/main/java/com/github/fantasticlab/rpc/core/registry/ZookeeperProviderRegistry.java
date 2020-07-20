package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.meta.Address;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;

import java.util.List;

public class ZookeeperProviderRegistry extends AbstractZookeeperRegister<ProviderNode> implements ProviderRegistry {

    public ZookeeperProviderRegistry(ZookeeperClient zookeeperClient) {
        super(zookeeperClient);
    }

    public static void testRegister(ZookeeperClient zookeeperClient, String service, String group, String address) throws FrpcRegistryException, InterruptedException {

        ProviderNode providerNode = new ProviderNode(group, service, address);

        ZookeeperProviderRegistry registry = new ZookeeperProviderRegistry(zookeeperClient);
        registry.register(providerNode);
    }

}
