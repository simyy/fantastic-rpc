package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.Discovery;
import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.util.ClientUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractDiscovery implements Discovery {

    protected Map<String, List<ProviderNode>> discoveryMap = new ConcurrentHashMap<>();

    protected Queue<RetryNode> retryNodeQueue = new ConcurrentLinkedDeque<>();

    protected AtomicBoolean running = new AtomicBoolean(false);

    protected Thread retryThread;

    protected RetryCallback retryCallback;

    @FunctionalInterface
    public interface RetryCallback {
        void run(String clientKey, String server, List<ProviderNode> nodes) throws InterruptedException;
    }


    public AbstractDiscovery(RetryCallback retryCallback) {
        this.retryCallback = retryCallback;
        this.running.set(true);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        retryThread = new Thread(() -> retryFailedNode());
        retryThread.setDaemon(true);
        retryThread.start();
    }

    @Data
    @AllArgsConstructor
    public static class RetryNode {
        private String service;
        private String group;
    }

    protected void retryFailedNode() {

        while (true) {

            log.info("Discovery loop retry start ...");

            List<RetryNode> faildNodeList = new ArrayList<>();

            while (!retryNodeQueue.isEmpty()) {
                RetryNode node = retryNodeQueue.poll();
                if (node != null) {
                    try {
                        List<ProviderNode> nodes = find(node.service, node.group, false);
                        if (nodes != null && nodes.size() > 0) {
                            this.retryCallback.run(ClientUtils.buildClientKey(nodes.get(0)), node.service, nodes);
                        }
                    } catch (FrpcInvokeException | InterruptedException e) {
                        log.error("Discovery retry failed {}/{}", node.service, node.group, e);
                        faildNodeList.add(node);
                    }
                }
            }

            if (!faildNodeList.isEmpty()) {
                retryNodeQueue.addAll(faildNodeList);
            }

            log.info("Discovery loop retry end");

            // loop scan between 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public void stop() {
        if (running.get() && retryThread != null) {
            retryThread.interrupt();
            running.set(false);
        }

        log.info("Discovery retry thread interrupt");

    }
}
