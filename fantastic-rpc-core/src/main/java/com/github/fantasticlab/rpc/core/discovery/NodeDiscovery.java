package com.github.fantasticlab.rpc.core.discovery;

public interface NodeDiscovery {

    String getService();

    String getGroup();

    void addWatcher(DiscoveryWatcher watcher);

    void removeWatcher(DiscoveryWatcher watcher);

}
