package com.github.fantasticlab.rpc.core.registry;

import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.exception.FrpcZkException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.PathUtil;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public abstract class ZKAbstractRegister {

    private ZkClient zkClient;

    private Map<String, BaseNode> registerMap = new ConcurrentHashMap<>();

    private Queue<BaseNode> retryRegisterNodeQueue = new ConcurrentLinkedDeque<>();
    private Queue<BaseNode> retryUnregisterNodeQueue = new ConcurrentLinkedDeque<>();

    private Thread retryThread;

    public ZKAbstractRegister(ZkClient zkClient) {
        this.zkClient = zkClient;

        retryThread = new Thread(() -> retryRegisterNode());
        retryThread.setDaemon(true);
        retryThread.start();
    }

    public void registerNode(BaseNode node) throws FrpcRegistryException {
        String path = PathUtil.buildPath(node);
        synchronized (registerMap) {
            BaseNode existNode = registerMap.get(path);
            if (existNode == null) {
                try {
                    zkClient.register(node.getService(), node.getGroup(), node.getNodeType(), node.getAddress().toString());
                } catch (FrpcZkException e) {
                    retryRegisterNodeQueue.add(node);
                    log.error("Register Failed:\t" + node.getNodeType().tag() + "\t" + path, e);
                    return;
                }
                registerMap.put(path, node);
            }
            log.info("Register\t" + node.getNodeType().tag() + "\t" + path);
        }
    }

    public void unregisterNode(BaseNode node) throws FrpcRegistryException {
        String path = PathUtil.buildPath(node);
        synchronized (registerMap) {
            BaseNode existNode = registerMap.get(path);
            if (existNode != null) {
                try {
                    zkClient.unregister(node.getService(), node.getGroup(), node.getNodeType(), node.getAddress().toString());
                } catch (Exception e) {
                    retryUnregisterNodeQueue.add(node);
                    log.error("Unregister Failed:\t" + node.getNodeType().tag() + "\t" + path, e);
                    return;
                }
                registerMap.remove(path);
            }
            log.info("Unregister\t" + node.getNodeType().tag() + "\t" + path);
        }
    }

    private void retryRegisterNode() {

        while (true) {

            log.debug("Register loop retry start ...");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            loopRetry(retryRegisterNodeQueue, this::registerNode);
            loopRetry(retryUnregisterNodeQueue, this::unregisterNode);

            log.debug("Register loop retry end");
        }
    }

    private void loopRetry(Queue<BaseNode> nodeQueue, RegisterFunc<BaseNode> func) {
        List<BaseNode> faildNodeList = new ArrayList<>();
        while (!nodeQueue.isEmpty()) {
            BaseNode baseNode = nodeQueue.poll();
            try {
                func.apply(baseNode);
            } catch (FrpcRegistryException e) {
                log.error("Register retry failed {}/{}", baseNode.getService(), baseNode.getGroup(), e);
                faildNodeList.add(baseNode);
            }
        }
        nodeQueue.addAll(faildNodeList);
    }

    @FunctionalInterface
    public interface RegisterFunc<T> {
        void apply(T t) throws FrpcRegistryException;
    }
}
