package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;

import java.util.List;

public interface Discovery {

    List<ProviderNode> find(String service, String group) throws FrpcInvokeException;

    List<ProviderNode> loadProviderNodes(String service, String group);

}
