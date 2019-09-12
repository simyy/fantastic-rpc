package com.github.fantasticlab.rpc.core.zookeeper;

import com.github.fantasticlab.rpc.core.meta.NodeType;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

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
    public void register(String serviceName, String group, NodeType nodeType, String address) {
        createPersistentNode(PREFIX + "/" + group);
        createPersistentNode(PREFIX + "/" + group + "/" + nodeType.tag());
        createPersistentNode(PREFIX + "/" + group + "/" + nodeType.tag() + "/" + serviceName);
        createEphemeralNode(PREFIX + "/" + group + "/" + nodeType.tag() + "/" + serviceName + "/" + address);
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
            this.zk.delete(PREFIX + "/" + service + "/" + address, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        ZkClient zkClient = new ZkClientImpl();
        assert zkClient != null;

        try {
            Thread.sleep (2000);
        } catch (InterruptedException e) {
        }

        System.out.println(">>> start registerNode");

        zkClient.register("hello", "test",  NodeType.PROVIDER, "127.0.0.1:8000");

        try {
            Thread.sleep (5000);
        } catch (InterruptedException e) {
        }

        System.out.println(">>> start unregisterNode");

        zkClient.unregister("hello", "test", NodeType.CONSUMER, "127.0.0.1:8000");

        try {
            Thread.sleep (10000) ;
        } catch (InterruptedException e) {
        }
    }

}
