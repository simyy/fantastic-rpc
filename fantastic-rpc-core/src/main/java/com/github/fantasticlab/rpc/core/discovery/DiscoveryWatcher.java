package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.meta.ProviderNode;

public interface DiscoveryWatcher {

    void onAdd(ProviderNode node);

    void onRemove(ProviderNode node);

}
