package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.exception.FrpcZkException;
import com.github.fantasticlab.rpc.core.meta.NodeType;

import java.util.List;
import java.util.function.Supplier;

public interface ZkClient {

    void register(String service, String group, NodeType nodeType, String address) throws FrpcZkException;

    void unregister(String service, String group, NodeType nodeType, String address) throws FrpcZkException;

    List<String> findAddress(String service, String group, NodeType nodeType) throws FrpcZkException;

    void addWatcher(String service, String group, NodeType nodeType, Supplier eventHandler) throws FrpcZkException;

}
