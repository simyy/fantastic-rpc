package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class ZookeeperClientImpl implements ZookeeperClient {

    private ZooKeeper zk;

    private static final String PREFIX = "/frpc";

    // "localhost:2181" for local test
    public ZookeeperClientImpl(String address) throws FrpcZookeeperException {

        try {
            this.zk = new ZooKeeper(address, 20000, null);
            createPersistentNode(PREFIX);
        } catch (Exception e) {
            String errMsg = "Zookeeper connect failed";
            log.error(errMsg, e);
            throw new FrpcZookeeperException(errMsg, e);
        }
    }

    @Override
    public void register(BaseNode node) throws FrpcZookeeperException {
        createPersistentNode(PathUtil.buildPath(PREFIX, node.getGroup()));
        createPersistentNode(PathUtil.buildPath(PREFIX, node.getGroup(), node.getNodeType().tag()));
        createPersistentNode(PathUtil.buildPath(PREFIX, node.getGroup(), node.getNodeType().tag(), node.getService()));
        createEphemeralNode(PathUtil.buildPath(PREFIX, node.getGroup(), node.getNodeType().tag(), node.getService(), node.getAddress().toString()));
    }


    private void createPersistentNode(String path) throws FrpcZookeeperException {
        try {
            this.zk.create(
                    path,
                    null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        } catch (KeeperException.NodeExistsException e) {
            // ignore
        } catch (Exception e) {
            String errMsg = "Zookeeper create persistent node failed";
            log.error(errMsg, e);
            throw new FrpcZookeeperException(errMsg, e);
        }

    }

    private void createEphemeralNode(String path) throws FrpcZookeeperException {
        try {
            this.zk.create(
                    path,
                    null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
        } catch (KeeperException.NodeExistsException e) {
            // ignore
        } catch (Exception e) {
            String errMsg = "Zookeeper create ephemeral node failed";
            log.error(errMsg, e);
            throw new FrpcZookeeperException(errMsg, e);
        }
    }

    @Override
    public void unregister(BaseNode node) throws FrpcZookeeperException {
        // -1 delete all node
        try {
            this.zk.delete(
                    PathUtil.buildPath(PREFIX,
                            node.getGroup(),
                            node.getNodeType().tag(),
                            node.getService(),
                            node.getAddress().toString()),
                    -1);
        } catch (Exception e) {
            String errMsg = "Zookeeper unregister failed";
            log.error(errMsg, e);
            throw new FrpcZookeeperException(errMsg, e);
        }
    }

    @Override
    public List<String> findAddress(String service, String group, NodeType nodeType) throws FrpcZookeeperException {
        try {
            return this.zk.getChildren(PathUtil.buildPath(PREFIX, group, nodeType.tag(), service), null);
        } catch (Exception e) {
            String errMsg = "Zookeeper findAddress failed";
            log.error(errMsg, e);
            throw new FrpcZookeeperException(errMsg, e);
        }
    }

    @Override
    public void addWatcher(String service, String group, NodeType nodeType, Supplier eventHandler) throws FrpcZookeeperException {
        addWatcher(
                PREFIX + "/" + group + "/" + nodeType.tag() + "/" + service,
                new ZKWatcher(this.zk, eventHandler));
    }

    private void addWatcher(String path, Watcher watcher) throws FrpcZookeeperException {
        try {
            this.zk.getChildren(path, watcher);
        } catch (Exception e) {
            String errMsg = "Zookeeper addWatcher failed";
            log.error(errMsg, e);
            throw new FrpcZookeeperException(errMsg, e);
        }
    }

}
