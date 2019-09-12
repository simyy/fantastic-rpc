package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.exception.RegistryException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;

public interface Registry<T extends BaseNode> {

    void register(T node) throws RegistryException;

    void unregister(T node) throws RegistryException;

}
