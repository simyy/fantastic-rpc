package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;

public class ZKProviderRegistry extends ZKAbstractRegister implements ProviderRegistry {

    public ZKProviderRegistry(ZkClient zkClient) {
        super(zkClient);
    }

    @Override
    public void available(ProviderNode node) throws FrpcRegistryException {
        // TODO
    }

    @Override
    public void unavailable(ProviderNode node) throws FrpcRegistryException {
        // TODO
    }

    @Override
    public void register(ProviderNode node) throws FrpcRegistryException {
       super.registerNode(node);
    }

    @Override
    public void unregister(ProviderNode node) throws FrpcRegistryException {
        super.unregisterNode(node);
    }

    public static void testRegister(ZkClient zkClient, String service, String group, String address) throws FrpcRegistryException, InterruptedException {

        ProviderNode providerNode = new ProviderNode();
        providerNode.setService(service);
        providerNode.setGroup(group);
        providerNode.setAddress(BaseNode.Address.parse(address));
        providerNode.setNodeType(NodeType.PROVIDER);

        ZKProviderRegistry registry = new ZKProviderRegistry(zkClient);
        registry.register(providerNode);
    }


    public static void main(String[] args) throws Exception {

        ZkClient zkClient = new ZkClientImpl("localhost:2181");
        ZKProviderRegistry.testRegister(zkClient, "hello", "test", "127.0.0.1:8000");

    }
}
