package com.github.fantasticlab.rpc.core.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.function.Supplier;

@Slf4j
public class ZKWatcher implements Watcher {

    private ZooKeeper zk;

    private Supplier eventHandler;

    public ZKWatcher(ZooKeeper zk, Supplier eventHandler) {
        this.zk = zk;
        this.eventHandler = eventHandler;
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("Zookeeper Watcher notify\t" + event);
        // handle message
        eventHandler.get();
        // set next watcher
        try {
            setNextWatcher(event);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setNextWatcher(WatchedEvent event) throws KeeperException, InterruptedException {

        Event.EventType type = event.getType();
        String path = event.getPath();
        switch (type) {
            case NodeCreated:
                break;
            case NodeDeleted:
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                this.zk.getChildren(path, this);
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            default:
                break;
        }
    }

}
