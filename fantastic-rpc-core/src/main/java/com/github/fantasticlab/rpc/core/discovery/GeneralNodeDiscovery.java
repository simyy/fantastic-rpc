//package com.github.fantasticlab.rpc.core.discovery;
//
//import com.github.fantasticlab.rpc.core.NodeDiscovery;
//import com.github.fantasticlab.rpc.core.DiscoveryWatcher;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//public class GeneralNodeDiscovery implements NodeDiscovery {
//
//    private String service;
//
//    private String group;
//
//    private List<DiscoveryWatcher> watchers = new CopyOnWriteArrayList<>();
//
//    public GeneralNodeDiscovery(String service, String group, List<DiscoveryWatcher> watchers) {
//        this.service = service;
//        this.group = group;
//        this.watchers = watchers;
//    }
//
//    @Override
//    public String getService() {
//        return service;
//    }
//
//    @Override
//    public String getGroup() {
//        return group;
//    }
//
//    @Override
//    public void addWatcher(DiscoveryWatcher watcher) {
//        watchers.add(watcher);
//    }
//
//    @Override
//    public void removeWatcher(DiscoveryWatcher watcher) {
//        watchers.remove(watcher);
//    }
//}
