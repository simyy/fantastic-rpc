package com.github.fantasticlab.rpc.core.balance;

import com.github.fantasticlab.rpc.core.meta.ProviderNode;

import java.util.List;

public interface LoadBalance {

    ProviderNode getOne(List<ProviderNode> nodes);

}
