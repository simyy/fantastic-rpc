package com.github.fantasticlab.rpc.core.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

@Slf4j
public class ZKWatcher implements Watcher {

    private ZooKeeper zk;

    public ZKWatcher(ZooKeeper zk) {
        this.zk = zk;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("==============> ZKWatcher event start <==============");
        // 循环监听
        try {
            this.zk.getChildren(event.getPath(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        print(event);
    }

    private void print(WatchedEvent event) {

        Event.EventType type = event.getType();
        Event.KeeperState state = event.getState();
        System.out.println("Path\t" + event.getPath());
        System.out.println("Type:\t" + type.getIntValue());
        System.out.println("State:\t" + state.getIntValue());
        switch (type) {
            case NodeCreated:
                System.out.println("NodeCreated");
                break;
            case NodeDeleted:
                System.out.println("NodeDeleted");
                break;
            case NodeDataChanged:
                System.out.println("NodeDataChanged");
                break;
            case NodeChildrenChanged:
                System.out.println("NodeChildrenChanged");
                break;
            case DataWatchRemoved:
                System.out.println("DataWatchRemoved");
                break;
            case ChildWatchRemoved:
                System.out.println("ChildWatchRemoved");
                break;
            default:
                System.out.println("Default");
                break;
        }
        switch (state) {
            case Disconnected:
                System.out.println("Disconnected");
                break;
            case SyncConnected:
                System.out.println("SyncConnected");
                break;
            case AuthFailed:
                System.out.println("AuthFailed");
                break;
            case ConnectedReadOnly:
                System.out.println("ConnectedReadOnly");
                break;
            case SaslAuthenticated:
                System.out.println("SaslAuthenticated");
                break;
            case Expired:
                System.out.println("Expired");
                break;
            case Closed:
                System.out.println("Closed");
                break;
            default:
                System.out.println("Default");
                break;
        }
        System.out.println("==============> ZKWatcher event end   <==============");
    }
}
