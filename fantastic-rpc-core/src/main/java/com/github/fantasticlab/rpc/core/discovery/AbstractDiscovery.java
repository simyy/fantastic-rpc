package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.Discovery;
import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
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

    public AbstractDiscovery() {
        running.set(true);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

            log.debug("Discovery loop retry start ...");

            // loop scan between 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }

            List<RetryNode> faildNodeList = new ArrayList<>();

            while (!retryNodeQueue.isEmpty()) {
                RetryNode node = retryNodeQueue.poll();
                if (node != null) {
                    try {
                        find(node.service, node.group);
                    } catch (FrpcInvokeException e) {
                        log.error("Discovery retry failed {}/{}", node.service, node.group, e);
                        faildNodeList.add(node);
                    }
                }
            }

            if (!faildNodeList.isEmpty()) {
                retryNodeQueue.addAll(faildNodeList);
            }

            log.debug("Discovery loop retry end");
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
