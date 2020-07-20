package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Slf4j
public class ZookeeperClientTest {

    @Test
    public void test() throws FrpcZookeeperException {

        ZookeeperClient zookeeperClient = new ZookeeperClientImpl("localhost:2181");
        assert zookeeperClient != null;

        String service = "TestService";
        String group = "jdkProxyTest";
        String address1 = "127.0.0.1:8000";
        String address2 = "127.0.0.1:8001";

        BaseNode node1 = new BaseNode(NodeType.PROVIDER, group, service, address1);
        BaseNode node2 = new BaseNode(NodeType.PROVIDER, group, service, address2);

        zookeeperClient.register(node1);
        zookeeperClient.register(node2);

        zookeeperClient.addWatcher(service, group, NodeType.PROVIDER,
                () -> {
                    log.info("NodeWatcher TestService/jdkProxyTest/provider");
                    return null;
                });


        List<String> rs1 = zookeeperClient.findAddress(service, group,  NodeType.PROVIDER);
        Assert.assertTrue(rs1.contains(address1) && rs1.contains(address2));
        zookeeperClient.unregister(node1);

        List<String> rs2 = zookeeperClient.findAddress(service, group,  NodeType.PROVIDER);
        Assert.assertTrue(!rs2.contains(address1) && rs2.contains(address2));
        zookeeperClient.unregister(node2);

        List<String> rs3 = zookeeperClient.findAddress(service, group,  NodeType.PROVIDER);
        Assert.assertTrue(!rs3.contains(address1) && !rs3.contains(address2));

    }

}
