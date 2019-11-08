package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;

import java.util.List;
import java.util.function.Supplier;

public interface ZookeeperClient {

    void register(BaseNode node) throws FrpcZookeeperException;

    void unregister(BaseNode node) throws FrpcZookeeperException;

    List<String> findAddress(String service, String group, NodeType nodeType) throws FrpcZookeeperException;

    void addWatcher(String service, String group, NodeType nodeType, Supplier eventHandler) throws FrpcZookeeperException;

}
