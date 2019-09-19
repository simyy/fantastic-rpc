package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.meta.NodeType;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ZkClientImpl implements ZkClient {

    private ZooKeeper zk;

    private static final String PREFIX = "/frpc";

    public ZkClientImpl() {
       init();
    }

    public ZkClientImpl init() {

        // TODO syncized~~~ dubble check

        try {
            this.zk = new ZooKeeper("localhost:2181", 2000, null);
            try {
                this.zk.getChildren(PREFIX, true);
            } catch (KeeperException | InterruptedException e) {
                // ignore
            }
            createPersistentNode(PREFIX);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void register(String service, String group, NodeType nodeType, String address) {
        createPersistentNode(PathUtil.buildPath(PREFIX, group));
        createPersistentNode(PathUtil.buildPath(PREFIX, group, nodeType.tag()));
        createPersistentNode(PathUtil.buildPath(PREFIX, group, nodeType.tag(), service));
        createPersistentNode(PathUtil.buildPath(PREFIX, group, nodeType.tag(), service, address));
    }


    private void createPersistentNode(String path) {
        try {
            this.zk.create(
                    path,
                    null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        } catch (KeeperException.NodeExistsException e) {
            // ignore
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createEphemeralNode(String path) {

        try {
            this.zk.create(
                    path,
                    null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
        } catch (KeeperException.NodeExistsException e) {
            // ignore
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unregister(String service, String group, NodeType nodeType, String address) {
        // -a delete all node
        try {
            this.zk.delete(PREFIX + "/" + group + "/" + nodeType.tag() + "/" + service + "/" + address, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> findAddress(String service, String group, NodeType nodeType) {

        try {
            return this.zk.getChildren(PREFIX + "/" + group + "/" + nodeType.tag() + "/" + service, null);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addWatcher(String service, String group, NodeType nodeType, Supplier eventHandler) {
        addWatcher(
                PREFIX + "/" + group + "/" + nodeType.tag() + "/" + service,
                new ZKWatcher(this.zk, eventHandler));
    }

    private void addWatcher(String path, Watcher watcher) {
        try {
            this.zk.getChildren(path, watcher);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        System.out.println("start");

        ZkClient zkClient = new ZkClientImpl();
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
