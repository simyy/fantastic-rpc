package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.Discovery;
import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.exception.FrpcZkException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.meta.PathUtil;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.registry.ZKProviderRegistry;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ZKServiceDiscovery extends AbstractDiscovery implements Discovery {

    private ZkClient zkClient;

    public ZKServiceDiscovery(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public List<ProviderNode> find(String service, String group) throws FrpcInvokeException {
        List<ProviderNode> nodes = loadProviderNodes(service, group);
        try {
            zkClient.addWatcher(service, group, NodeType.PROVIDER, () -> loadProviderNodes(service, group));
        } catch (FrpcZkException e) {
            String errMsg = "Discovery find failed";
            log.error(errMsg, e);
            throw new FrpcInvokeException(errMsg, e);
        }
        return nodes;
    }

    @Override
    public List<ProviderNode> loadProviderNodes(String service, String group) {
        String path = PathUtil.buildProviderPath(service, group);
        List<ProviderNode> nodes = discoveryMap.get(path);
        if (nodes == null) {
            synchronized (discoveryMap) {
                nodes = discoveryMap.get(path);
                if (nodes == null) {

                    List<String> addressList = null;
                    try {
                        addressList = zkClient.findAddress(service, group, NodeType.PROVIDER);
                    } catch (FrpcZkException e) {
                        log.error("Discovery load nodes failed {}/{}/{}", service, group, NodeType.PROVIDER.tag(), e);
                        retryNodeQueue.add(new RetryNode(service, group));
                        return new ArrayList<>();
                    }

                    log.info("Discovery:\t" + path + "\t\tAddress:\t" + addressList);
                    if (CollectionUtils.isEmpty(addressList)) {
                        return new ArrayList<>();
                    }
                    nodes = addressList.stream()
                            .map(address -> {
                                ProviderNode node = new ProviderNode();
                                node.setAddress(BaseNode.Address.parse(address));
                                node.setService(service);
                                node.setGroup(group);
                                node.setNodeKey(path);
                                Long now = new Date().getTime();
                                node.setRefreshTime(now);
                                node.setRegisterTime(now);
                                return node;
                            })
                            .collect(Collectors.toList());
                    discoveryMap.put(path, nodes);
                }
            }
        }
        return nodes;
    }


    public static void main(String[] args) throws InterruptedException, FrpcRegistryException, FrpcInvokeException, FrpcZkException {

        ZkClient zkClient = new ZkClientImpl("localhost:2181");
//        System.out.print("Register\t127.0.0.1:8000\n");
//        ZKProviderRegistry.testRegister(zkClient, "hello", "test", "127.0.0.1:8000");

        ZKServiceDiscovery zkServiceDiscovery = new ZKServiceDiscovery(zkClient);
        List<ProviderNode> nodes = zkServiceDiscovery.find("hello", "test");
        Thread.sleep(2000);
        System.out.print("Register\t127.0.0.2:8000\n");
        ZKProviderRegistry.testRegister(zkClient, "hello", "test", "127.0.0.2:8000");
        Thread.sleep(2000);
        System.out.print("Register\t127.0.0.3:8000\n");
        ZKProviderRegistry.testRegister(zkClient, "hello", "test", "127.0.0.3:8000");
        Thread.sleep(5000);

        zkServiceDiscovery.stop();

    }

}
