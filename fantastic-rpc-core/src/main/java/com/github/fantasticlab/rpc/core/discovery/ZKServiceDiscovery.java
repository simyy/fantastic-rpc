package com.github.fantasticlab.rpc.core.discovery;

import com.github.fantasticlab.rpc.core.Discovery;
import com.github.fantasticlab.rpc.core.exception.InvokeException;
import com.github.fantasticlab.rpc.core.exception.RegistryException;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.meta.PathUtil;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.registry.ZKProviderRegistry;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ZKServiceDiscovery implements Discovery {

    private ZkClient zkClient;

    public ZKServiceDiscovery(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    private Map<String, List<ProviderNode>> discoveryMap = new HashMap<>();

    @Override
    public List<ProviderNode> find(String service, String group) throws InvokeException {
        List<ProviderNode> nodes =loadProviderNodes(service, group);
        zkClient.addWatcher(service, group, NodeType.PROVIDER, () -> loadProviderNodes(service, group));
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
                    List<String> addressList = zkClient.findAddress(service, group, NodeType.PROVIDER);
                    if (CollectionUtils.isEmpty(addressList)) {
                        // l
                    }
                    System.out.println("Discovery:" + path + "\t Address:" + addressList);
                    nodes = addressList.stream()
                            .map(address -> {
                                ProviderNode node = new ProviderNode();
                                node.setAddress(address);
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


    public static void main(String[] args) throws InterruptedException, RegistryException, InvokeException {

        ZkClient zkClient = new ZkClientImpl();
        System.out.print("Register\t127.0.0.1:8000");
        ZKProviderRegistry.testRegister(zkClient, "hello", "test", "127.0.0.1:8000");

        ZKServiceDiscovery zkServiceDiscovery = new ZKServiceDiscovery(zkClient);
        List<ProviderNode> nodes = zkServiceDiscovery.find("hello", "test");
        Thread.sleep(5000);
        System.out.print("Register\t127.0.0.2:8000");
        ZKProviderRegistry.testRegister(zkClient, "hello", "test", "127.0.0.2:8000");
        Thread.sleep(5000);
        System.out.print("Register\t127.0.0.3:8000");
        ZKProviderRegistry.testRegister(zkClient, "hello", "test", "127.0.0.3:8000");
        Thread.sleep(5000);


    }

}
