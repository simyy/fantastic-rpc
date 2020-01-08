package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.exception.FrpcException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;

import java.util.List;

public interface Discovery {

    /**
     * Find the service/group in cache, if not exist,
     * pull from configuration(ex zookeeper and etcd).
     *
     * @param service
     * @param group
     * @return
     * @throws FrpcException
     */
    List<ProviderNode> find(String service, String group) throws FrpcException;

    /**
     * Reload the service/group, even through cached.
     *
     * @param service
     * @param group
     * @return
     * @throws FrpcException
     */
    List<ProviderNode> reload(String service, String group) throws FrpcException;

}
