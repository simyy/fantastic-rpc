package com.github.fantasticlab.rpc.core;

public interface NodeDiscovery {

    String getService();

    String getGroup();

    void addWatcher(DiscoveryWatcher watcher);

    void removeWatcher(DiscoveryWatcher watcher);

}
