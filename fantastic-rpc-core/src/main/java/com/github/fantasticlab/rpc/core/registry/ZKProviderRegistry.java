package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.RegistryException;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;

public class ZKProviderRegistry extends ZKAbstractRegister implements ProviderRegistry {

    public ZKProviderRegistry(ZkClient zkClient) {
        super(zkClient);
    }

    @Override
    public void available(ProviderNode node) throws RegistryException {
        // TODO
    }

    @Override
    public void unavailable(ProviderNode node) throws RegistryException {
        // TODO
    }

    @Override
    public void register(ProviderNode node) throws RegistryException {
       super.registerNode(node);
    }

    @Override
    public void unregister(ProviderNode node) throws RegistryException {
        super.unregisterNode(node);
    }


    public static void main(String[] args) throws RegistryException, InterruptedException {

        ProviderNode providerNode = new ProviderNode();
        providerNode.setService("helloService");
        providerNode.setGroup("test");
        providerNode.setAddress("127.0.0.1:8000");
        providerNode.setNodeType(NodeType.PROVIDER);

        ZkClient zkClient = new ZkClientImpl();

        ZKProviderRegistry registry = new ZKProviderRegistry(zkClient);
        registry.register(providerNode);

        Thread.sleep(30000);

    }
}
