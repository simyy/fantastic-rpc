package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.Discovery;
import com.github.fantasticlab.rpc.core.exception.FrpcException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.util.PoolUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public abstract class AbstractDiscovery implements Discovery {
    /**
     * DiscoveryPool store all service node of discovery.
     * DiscoveryPool.key is generate by PoolUtil.buildKey.
     */
    protected Map<String, List<ProviderNode>> discoveryPool = new ConcurrentHashMap<>();
    /**
     * RetryNodeQueue records all failed node of find.
     */
    protected Queue<RetryNode> retryNodeQueue = new ConcurrentLinkedDeque<>();
    /**
     * FailedNodeQueue records all failed node in retry loop, to avoid multi thread problem.
     */
    protected Queue<RetryNode> failedNodeQueue = new ConcurrentLinkedDeque<>();

    protected Thread retryThread;

    public AbstractDiscovery() {
        retryThread = new Thread(() -> retryLoop());
        retryThread.setDaemon(true);
        retryThread.start();
    }

    abstract List<ProviderNode> find(String service, String group, boolean autoRetry) throws FrpcException;

    @Override
    public List<ProviderNode> reload(String service, String group) throws FrpcException {
        discoveryPool.remove(PoolUtil.buildKey(service, group));
        return find(service, group);
    }

    @Data
    @AllArgsConstructor
    public static class RetryNode {
        private String service;
        private String group;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RetryNode retryNode = (RetryNode) o;
            return Objects.equals(service, retryNode.service) &&
                    Objects.equals(group, retryNode.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(service, group);
        }
    }



    protected void retryLoop() {
        while (true) {
            log.info("[RetryLoop] size={}", retryNodeQueue.size());
            while (!retryNodeQueue.isEmpty()) {
                RetryNode node = retryNodeQueue.poll();
                if (node != null) {
                    try {
                        find(node.service, node.group, false);
                    } catch (FrpcException e) {
                        log.error("[RetryLoop] failed {}/{}", node.service, node.group, e);
                        failedNodeQueue.add(node);
                    }
                }
            }
            while (!failedNodeQueue.isEmpty()) {
                retryNodeQueue.add(failedNodeQueue.poll());
            }
            // loop scan between 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignore) {
            }
        }
    }


//    public void stop(int seconds) {
//        if (isRunning.get() && retryThread != null) {
//            retryThread.interrupt();
//            isRunning.set(false);
//        }
//        log.info("Discovery stopped ...");
//    }
}
