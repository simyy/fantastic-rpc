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
}
