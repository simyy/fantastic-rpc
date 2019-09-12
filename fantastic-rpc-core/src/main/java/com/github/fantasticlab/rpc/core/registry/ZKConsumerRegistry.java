package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.RegistryException;
import com.github.fantasticlab.rpc.core.meta.ConsumerNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;

public class ZKConsumerRegistry extends ZKAbstractRegister implements ConsumerRegistry {

    public ZKConsumerRegistry(ZkClient zkClient) {
        super(zkClient);
    }

    @Override
    public void register(ConsumerNode node) throws RegistryException {
        super.registerNode(node);
    }

    @Override
    public void unregister(ConsumerNode node) throws RegistryException {
        super.unregisterNode(node);
    }

    public static void main(String[] args) throws RegistryException, InterruptedException {

        ConsumerNode consumerNode = new ConsumerNode();
        consumerNode.setService("helloService");
        consumerNode.setGroup("test");
        consumerNode.setAddress("127.0.0.1:8000");
        consumerNode.setNodeType(NodeType.CONSUMER);

        ZkClient zkClient = new ZkClientImpl();

        ZKConsumerRegistry registry = new ZKConsumerRegistry(zkClient);
        registry.register(consumerNode);

        Thread.sleep(30000);

    }
}
