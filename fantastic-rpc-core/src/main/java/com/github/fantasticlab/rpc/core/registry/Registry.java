package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;

public interface Registry<T extends BaseNode> {

    void register(T node) throws FrpcRegistryException;

    void unregister(T node) throws FrpcRegistryException;

}
