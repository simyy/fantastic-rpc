package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.Registry;
import com.github.fantasticlab.rpc.core.exception.RegistryException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.PathUtil;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ZKAbstractRegister {

    private ZkClient zkClient;

    private Map<String, BaseNode> registerMap = new ConcurrentHashMap<>();

    public ZKAbstractRegister(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public void registerNode(BaseNode node) throws RegistryException {
        String path = PathUtil.buildPath(node);
        synchronized (registerMap) {
            BaseNode existNode = registerMap.get(path);
            if (existNode == null) {
                zkClient.register(node.getService(), node.getGroup(), node.getNodeType(), node.getAddress());
                registerMap.put(path, node);
            }
            System.out.println("Register:\t" + node.getNodeType().tag() + "\t" + path);
        }
    }

    public void unregisterNode(BaseNode node) throws RegistryException {
        String path = PathUtil.buildPath(node);
        synchronized (registerMap) {
            BaseNode existNode = registerMap.get(path);
            if (existNode != null) {
                zkClient.unregister(node.getService(), node.getGroup(), node.getNodeType(), node.getAddress());
                registerMap.remove(path);
            }
            System.out.println("Unregister:\t" + node.getNodeType().tag() + "\t" + path);
        }
    }
}
