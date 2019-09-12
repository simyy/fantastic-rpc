package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.meta.NodeType;
import org.apache.zookeeper.ZooKeeper;

public interface ZkClient {

    void register(String service, String group, NodeType nodeType, String address);

    void unregister(String service, String group, NodeType nodeType, String address);

}
