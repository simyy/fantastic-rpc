package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.discovery.Discovery;
import com.github.fantasticlab.rpc.core.discovery.ZooKeeperServiceDiscovery;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.registry.Registry;
import com.github.fantasticlab.rpc.core.registry.ZookeeperProviderRegistry;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class ZooKeeperRegistryAndDiscoveryTest {

    private ZookeeperClient zookeeperClient;
    private Registry registry;
    private Discovery discovery;

    @Before
    public void setUp() throws Exception {
        zookeeperClient = new ZookeeperClientImpl("localhost:2181");
        discovery = new ZooKeeperServiceDiscovery(zookeeperClient);
        registry = new ZookeeperProviderRegistry(zookeeperClient);
    }

    @Test
    public void test() {

        String service = "TestService";
        String group = "jdkProxyTest";
        String address1 = "127.0.0.2:8000";
        String address2 = "127.0.0.3:8000";

        ProviderNode node1 = new ProviderNode(group, service, address1);
        ProviderNode node2 = new ProviderNode(group, service, address2);

        registry.register(node1);
        registry.register(node2);

        List<ProviderNode> providerNodes = discovery.find(service, group);
        assert providerNodes != null;
        List<String> addresses = providerNodes.stream()
                .map(ProviderNode::getAddress)
                .map(p -> p.getHost() + ":" + p.getPort())
                .collect(Collectors.toList());
        assert addresses.contains(address1) && addresses.contains(address2);


    }

}
