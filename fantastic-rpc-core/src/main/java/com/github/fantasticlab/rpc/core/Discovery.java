package com.github.fantasticlab.rpc.core;

public interface Discovery {

    String getService();

    String getGroup();

    void addWatcher(DiscoveryWatcher watcher);

    void removeWatcher(DiscoveryWatcher watcher);

}
