package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.exception.FrpcException;
import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.util.PoolUtil;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ZooKeeperServiceDiscovery extends AbstractDiscovery implements Discovery {

    private ZookeeperClient zookeeperClient;

    public ZooKeeperServiceDiscovery(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }


    @Override
    public List<ProviderNode> find(String service, String group) throws FrpcInvokeException {
        return find(service, group, true);
    }

    public List<ProviderNode> find(String service, String group, boolean autoRetry) throws FrpcInvokeException {
        List<ProviderNode> nodes = loadProviderNodes(service, group, autoRetry, false);
        if (nodes != null && nodes.size() > 0) {
            try {
                zookeeperClient.addWatcher(service, group, NodeType.PROVIDER, () -> loadProviderNodes(service, group, true, true));
            } catch (FrpcZookeeperException e) {
                String errMsg = "Discovery find failed";
                log.error(errMsg, e);
                throw new FrpcInvokeException(errMsg, e);
            }
        }
        return nodes;
    }

    private List<ProviderNode> loadProviderNodes(String service, String group, boolean autoRetry, boolean reload) throws FrpcException {
        String serviceKey = PoolUtil.buildKey(service, group);
        if (reload) {
            discoveryPool.remove(serviceKey);
        }
        List<ProviderNode> nodes = discoveryPool.get(serviceKey);
        if (nodes == null || nodes.size() == 0) {
            synchronized (discoveryPool) {
                nodes = discoveryPool.get(serviceKey);
                if (nodes == null || nodes.size() == 0) {

                    List<String> addressList;
                    try {
                        addressList = zookeeperClient.findAddress(service, group, NodeType.PROVIDER);
                    } catch (FrpcZookeeperException e) {
                        log.error("[Discovery] find nodes failed {}/{}/{}", service, group, NodeType.PROVIDER.tag(), e);
                        if (autoRetry) {
                            push2RetryQueue(service, group);
                            return new ArrayList<>();
                        }
                        throw new FrpcException("[Discovery] find nodes failed", e);
                    }

                    if (addressList == null && addressList.size() == 0) {
                        log.error("[Discovery] find nodes failed {}/{}/{} not exist", service, group, NodeType.PROVIDER.tag());
                        if (autoRetry) {
                            push2RetryQueue(service, group);
                            return new ArrayList<>();
                        }
                        throw new FrpcException("Discovery load nodes failed", null);
                    }

                    log.info("[Discovery] find nodes {}/{}/{} {}", service, group, NodeType.PROVIDER.tag(), addressList);

                    nodes = addressList.stream()
                            .map(address -> {
                                ProviderNode node = new ProviderNode(group, service, address);
                                Long now = new Date().getTime();
                                node.setRefreshTime(now);
                                node.setRegisterTime(now);
                                return node;
                            })
                            .collect(Collectors.toList());
                    discoveryPool.put(serviceKey, nodes);
                }
            }
        }
        return nodes;
    }

    private void push2RetryQueue(String service, String group) {
        RetryNode node = new RetryNode(service, group);
        if (!retryNodeQueue.contains(node)) {
            retryNodeQueue.add(new RetryNode(service, group));
        }
    }


    public static void main(String[] args) throws InterruptedException, FrpcRegistryException, FrpcInvokeException, FrpcZookeeperException {

        ZookeeperClient zookeeperClient = new ZookeeperClientImpl("localhost:2181");
//        System.out.print("Register\t127.0.0.1:8000\n");
//        ZookeeperProviderRegistry.testRegister(zookeeperClient, "hello", "test", "127.0.0.1:8000");

        ZooKeeperServiceDiscovery zooKeeperServiceDiscovery = new ZooKeeperServiceDiscovery(zookeeperClient);
//        List<ProviderNode> nodes = zooKeeperServiceDiscovery.find("hello", "test");
//        Thread.sleep(2000);
//        System.out.print("Register\t127.0.0.2:8000\n");
//        ZookeeperProviderRegistry.testRegister(zookeeperClient, "hello", "test", "127.0.0.2:8000");
//        Thread.sleep(2000);
//        System.out.print("Register\t127.0.0.3:8000\n");
//        ZookeeperProviderRegistry.testRegister(zookeeperClient, "hello", "test", "127.0.0.3:8000");
//        Thread.sleep(5000);

        try {
            zooKeeperServiceDiscovery.find("HelloService", "test");
        } catch (Exception e) {

        }

        Thread.sleep(300000);

    }

}
