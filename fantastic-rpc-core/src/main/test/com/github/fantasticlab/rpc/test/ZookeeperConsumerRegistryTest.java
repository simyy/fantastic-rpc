package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.ConsumerNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.registry.ZookeeperConsumerRegistry;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperConsumerRegistryTest {

    private ZookeeperClient zookeeperClient;

    @Test
    public void test() throws FrpcZookeeperException {
        zookeeperClient = new ZookeeperClientImpl("localhost:2181");
        ConsumerNode consumerNode = new ConsumerNode("jdkProxyTest", "helloService", "127.0.0.1:8000");
        consumerNode.setNodeType(NodeType.CONSUMER);
        ZookeeperConsumerRegistry registry = new ZookeeperConsumerRegistry(zookeeperClient);
        registry.register(consumerNode);
    }
}
