package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.Registry;
import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;

public interface ProviderRegistry extends Registry<ProviderNode> {


    void available(ProviderNode node) throws FrpcRegistryException;


    void unavailable(ProviderNode node) throws FrpcRegistryException;


}
