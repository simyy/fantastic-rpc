package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.balance.LoadBalance;
import com.github.fantasticlab.rpc.core.balance.RandomLoadBalance;
import com.github.fantasticlab.rpc.core.discovery.ZKServiceDiscovery;
import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.exception.FrpcZkException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.net.NettyClient;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.test.HelloService;
import com.github.fantasticlab.rpc.core.util.ClientUtils;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcClient  {

    //localhost:2181
    private String zk;

    private String group;

    private ZkClient zkClient;

    private ZKServiceDiscovery zkServiceDiscovery;

    private Map<String, NettyClient> nettyClientMap = new ConcurrentHashMap<>();


    public RpcClient(String zk, String group) throws FrpcZkException, InterruptedException {
        this.zk = zk;
        this.group = group;

        // init zookeeper
        this.zkClient = new ZkClientImpl(this.zk);
        // TODO  init consumer registry
        // init discovery
        this.zkServiceDiscovery = new ZKServiceDiscovery(this.zkClient, this::initNettyClient);
    }

    public Object invoke(String service, String method, Class<?>[] argTypes, Object[] args) throws InterruptedException {

        String clientKey = ClientUtils.buildClientKey(service, this.group);
        loadNettyClient(clientKey, service);
        NettyClient nettyClient = nettyClientMap.get(clientKey);
        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService(service);
        reqPacket.setMethod(method);
        reqPacket.setArgTypes(argTypes);
        reqPacket.setArgs(args);
        return nettyClient.send(reqPacket);
    }

    private void loadNettyClient(String clientKey, String service) throws InterruptedException {
        List<ProviderNode> nodes = this.zkServiceDiscovery.find(service, this.group, true);
        if (CollectionUtils.isEmpty(nodes)) {
            String errMsg = "RpcClient service not found\t" + service + "|" + group;
            log.error(errMsg);
            throw new FrpcInvokeException(errMsg, null);
        }
        initNettyClient(clientKey, service, nodes);
    }

    public void closedCallback(String clientKey, String service) {
        nettyClientMap.remove(clientKey);
    }

    public void initNettyClient(String clientKey, String service, List<ProviderNode> nodes) throws InterruptedException {
        // TODO multi load balance
        LoadBalance loadBalance = new RandomLoadBalance();
        ProviderNode providerNode = loadBalance.getOne(nodes);

        if (!nettyClientMap.containsKey(clientKey)) {
            synchronized (this) {
                if (!nettyClientMap.containsKey(clientKey)) {
                    NettyClient nettyClient = new NettyClient(
                            providerNode.getAddress().getHost(),
                            providerNode.getAddress().getPort(),
                            () -> closedCallback(clientKey, service));
                    nettyClient.connect();
                    nettyClientMap.put(clientKey, nettyClient);
                }
            }
        }
    }

    public static void main(String[] args ) throws InterruptedException, FrpcZkException {

        String zk = "localhost:2181";
        String group = "test";

        RpcClient client = new RpcClient(zk, group);

        Class<?>[] argTypes = new Class<?>[1];
        argTypes[0] = String.class;
        Object[] argss = new Object[1];
        argss[0] = "Jack";

        while (true) {

            try {
                String rs1 = (String) client.invoke(HelloService.class.getSimpleName(), "sayHi", null, null);
                log.info(rs1);
            } catch (FrpcInvokeException e) {
                log.error("FrpcInvokeException",  e);
            }

            Thread.sleep(8000);

//            try {
//                String rs2 = (String) client.invoke(HelloService.class.getSimpleName(), "sayHi", argTypes, argss);
//            System.out.println(rs2);
//            } catch (FrpcInvokeException e) {
//
//            }
        }
    }

}
