package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.exception.FrpcZkException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.ConsumerNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;

public class ZKConsumerRegistry extends ZKAbstractRegister implements ConsumerRegistry {

    public ZKConsumerRegistry(ZkClient zkClient) {
        super(zkClient);
    }

    @Override
    public void register(ConsumerNode node) throws FrpcRegistryException {
        super.registerNode(node);
    }

    @Override
    public void unregister(ConsumerNode node) throws FrpcRegistryException {
        super.unregisterNode(node);
    }

    public static void main(String[] args) throws FrpcRegistryException, InterruptedException, FrpcZkException {

        ConsumerNode consumerNode = new ConsumerNode();
        consumerNode.setService("helloService");
        consumerNode.setGroup("test");
        consumerNode.setAddress(BaseNode.Address.parse("127.0.0.1:8000"));
        consumerNode.setNodeType(NodeType.CONSUMER);

        ZkClient zkClient = new ZkClientImpl("localhost:2181");

        ZKConsumerRegistry registry = new ZKConsumerRegistry(zkClient);
        registry.register(consumerNode);

        Thread.sleep(30000);

    }
}
