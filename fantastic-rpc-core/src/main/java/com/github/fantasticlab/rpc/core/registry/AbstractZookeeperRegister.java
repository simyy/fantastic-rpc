package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.Registry;
import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.util.PoolUtil;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public abstract class AbstractZookeeperRegister<T extends BaseNode> implements Registry<T> {

    private ZookeeperClient zookeeperClient;
    /**
     * RegisterPool store all node of registered.
     * Key of RegisterPool is the registry path (Use PoolUtil.buildPath).
     */
    private Map<String, BaseNode> registryPool = new ConcurrentHashMap<>();
    /**
     * RetryRegisterNodeQueue records the failed node of register.
     */
    private Queue<T> retryRegisterNodeQueue = new ConcurrentLinkedDeque<>();
    /**
     * RetryUnregisterNodeQueue records the failed node of unregister.
     */
    private Queue<T> retryUnregisterNodeQueue = new ConcurrentLinkedDeque<>();

    private Thread retryThread;

    public AbstractZookeeperRegister(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
        retryThread = new Thread(() -> retryLoop());
        retryThread.setDaemon(true);
        retryThread.start();
    }

    @Override
    public void register(T node) throws FrpcRegistryException {
        register(node, true);
    }

    public void register(T node, boolean retry) throws FrpcRegistryException {
        String path = PoolUtil.buildPath(node);
        if (registryPool.containsKey(path)) {
            return;
        }
        try {
            zookeeperClient.register(node);
            registryPool.put(path, node);
            log.info("Register node={}", node, path);
        } catch (FrpcZookeeperException e) {
            if (retry) {
                retryRegisterNodeQueue.add(node);
            }
            log.error("Register Failed node={}", node, e);
            return;
        }
    }

    @Override
    public void unregister(T node) throws FrpcRegistryException {
        unregister(node, true);
    }

    public void unregister(T node, boolean retry) throws FrpcRegistryException {
        String path = PoolUtil.buildPath(node);
        if (!registryPool.containsKey(path)) {
            return;
        }
        try {
            zookeeperClient.unregister(node);
            registryPool.remove(path);
            log.info("Unregister node={}", node);
        } catch (FrpcZookeeperException e) {
            if (retry) {
                retryUnregisterNodeQueue.add(node);
            }
            log.error("Unregister Failed node={}", node, e);
            return;
        }
    }

    private void retryLoop() {

        while (true) {
            log.debug("[RetryLoop] registry={} unregister={}",
                    retryRegisterNodeQueue.size(), retryUnregisterNodeQueue.size());
            if (!retryRegisterNodeQueue.isEmpty() || !retryUnregisterNodeQueue.isEmpty()) {
                loopRetry(retryRegisterNodeQueue, this::register);
                loopRetry(retryUnregisterNodeQueue, this::unregister);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private void loopRetry(Queue<T> nodeQueue, RegisterFunc<T> func) {
        List<T> failedList = new ArrayList<>();
        while (!nodeQueue.isEmpty()) {
            T node = nodeQueue.poll();
            try {
                func.apply(node);
            } catch (FrpcRegistryException e) {
                log.error("[RetryLoop] failed  node={}", node, e);
                failedList.add(node);
            }
        }
        nodeQueue.addAll(failedList);
    }

    @FunctionalInterface
    public interface RegisterFunc<T> {
        void apply(T t) throws FrpcRegistryException;
    }
}
