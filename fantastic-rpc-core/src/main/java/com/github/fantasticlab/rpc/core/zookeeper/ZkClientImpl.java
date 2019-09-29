package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.exception.FrpcZkException;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class ZkClientImpl implements ZkClient {

    private ZooKeeper zk;

    private static final String PREFIX = "/frpc";

    // "localhost:2181" for local test
    public ZkClientImpl(String address) throws FrpcZkException {

        try {
            this.zk = new ZooKeeper(address, 20000, null);
            createPersistentNode(PREFIX);
        } catch (Exception e) {
            String errMsg = "Zookeeper connect failed";
            log.error(errMsg, e);
            throw new FrpcZkException(errMsg, e);
        }
    }

    @Override
    public void register(String service, String group, NodeType nodeType, String address) throws FrpcZkException {
        createPersistentNode(PathUtil.buildPath(PREFIX, group));
        createPersistentNode(PathUtil.buildPath(PREFIX, group, nodeType.tag()));
        createPersistentNode(PathUtil.buildPath(PREFIX, group, nodeType.tag(), service));
        createEphemeralNode(PathUtil.buildPath(PREFIX, group, nodeType.tag(), service, address));
    }


    private void createPersistentNode(String path) throws FrpcZkException {
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
            throw new FrpcZkException(errMsg, e);
        }

    }

    private void createEphemeralNode(String path) throws FrpcZkException {
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
            throw new FrpcZkException(errMsg, e);
        }
    }

    @Override
    public void unregister(String service, String group, NodeType nodeType, String address) throws FrpcZkException {
        // -1 delete all node
        try {
            this.zk.delete(PREFIX + "/" + group + "/" + nodeType.tag() + "/" + service + "/" + address, -1);
        } catch (Exception e) {
            String errMsg = "Zookeeper unregister failed";
            log.error(errMsg, e);
            throw new FrpcZkException(errMsg, e);
        }
    }

    @Override
    public List<String> findAddress(String service, String group, NodeType nodeType) throws FrpcZkException {
        try {
            return this.zk.getChildren(PathUtil.buildPath(PREFIX, group, nodeType.tag(), service), null);
        } catch (Exception e) {
            String errMsg = "Zookeeper findAddress failed";
            log.error(errMsg, e);
            throw new FrpcZkException(errMsg, e);
        }
    }

    @Override
    public void addWatcher(String service, String group, NodeType nodeType, Supplier eventHandler) throws FrpcZkException {
        addWatcher(
                PREFIX + "/" + group + "/" + nodeType.tag() + "/" + service,
                new ZKWatcher(this.zk, eventHandler));
    }

    private void addWatcher(String path, Watcher watcher) throws FrpcZkException {
        try {
            this.zk.getChildren(path, watcher);
        } catch (Exception e) {
            String errMsg = "Zookeeper addWatcher failed";
            log.error(errMsg, e);
            throw new FrpcZkException(errMsg, e);
        }
    }

    public static void main(String[] args) throws FrpcZkException {

        System.out.println("start");

        ZkClient zkClient = new ZkClientImpl("localhost:2181");
        assert zkClient != null;

        System.out.println(">>> 1. add watcher");
        zkClient.addWatcher("hello", "test", NodeType.PROVIDER, () -> { System.out.println("NodeWatcher hello/test/provider"); return null;});


        System.out.println(">>> 2. registerNode");
        zkClient.register("hello", "test",  NodeType.PROVIDER, "127.0.0.1:8000");

        List<String> addresses = zkClient.findAddress("hello", "test",  NodeType.PROVIDER);
        System.out.println(">>> 3. findAddress rs=" + addresses);


        System.out.println(">>> 4. unregisterNode");
        zkClient.unregister("hello", "test", NodeType.PROVIDER, "127.0.0.1:8000");

        List<String> addresses1 = zkClient.findAddress("hello", "test",  NodeType.PROVIDER);
        System.out.println(">>> 5. findAddress rs=" + addresses1);

        System.out.println(">>> 6. registerNode");
        zkClient.register("hello", "test",  NodeType.PROVIDER, "127.0.0.1:8001");

        List<String> addresses2 = zkClient.findAddress("hello", "test",  NodeType.PROVIDER);
        System.out.println(">>> 7. findAddress rs=" + addresses2);

        System.out.println(">>> 8. unregisterNode");
        zkClient.unregister("hello", "test", NodeType.PROVIDER, "127.0.0.1:8001");

        System.out.println("end");

    }

}
