package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.ConsumerNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;

public class ZookeeperConsumerRegistry extends AbstractZookeeperRegister<ConsumerNode> implements ConsumerRegistry {

    public ZookeeperConsumerRegistry(ZookeeperClient zookeeperClient) {
        super(zookeeperClient);
    }

    public static void main(String[] args) throws FrpcRegistryException, InterruptedException, FrpcZookeeperException {

        ConsumerNode consumerNode = new ConsumerNode("test", "helloService", "127.0.0.1:8000");
        consumerNode.setNodeType(NodeType.CONSUMER);

        ZookeeperClient zookeeperClient = new ZookeeperClientImpl("localhost:2181");

        ZookeeperConsumerRegistry registry = new ZookeeperConsumerRegistry(zookeeperClient);
        registry.register(consumerNode);

        Thread.sleep(30000);

    }
}
