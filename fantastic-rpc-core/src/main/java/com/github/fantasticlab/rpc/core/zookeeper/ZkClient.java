package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.meta.NodeType;
import jdk.nashorn.internal.objects.annotations.Function;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ZkClient {

    void register(String service, String group, NodeType nodeType, String address);

    void unregister(String service, String group, NodeType nodeType, String address);

    List<String> findAddress(String service, String group, NodeType nodeType);

    void addWatcher(String service, String group, NodeType nodeType, Supplier eventHandler);

}
