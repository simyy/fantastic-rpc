package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.balance.LoadBalance;
import com.github.fantasticlab.rpc.core.balance.RandomLoadBalance;
import com.github.fantasticlab.rpc.core.discovery.ZooKeeperServiceDiscovery;
import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.net.NettyClient;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.test.HelloService;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public class RpcClient  {

    //localhost:2181
    private String zk;

    private String group;

    private ZookeeperClient zookeeperClient;

    private ZooKeeperServiceDiscovery zooKeeperServiceDiscovery;

    private Thread retryClientThread;

    private Queue<NettyClient> retryClientQueue = new ConcurrentLinkedDeque<>();
    private Queue<NettyClient> failedClientQueue = new ConcurrentLinkedDeque<>();
    // { service : netty client}
    private Map<String, NettyClient> clientPool = new ConcurrentHashMap<>();


    public RpcClient(String zk, String group) throws FrpcZookeeperException, InterruptedException {
        this.zk = zk;
        this.group = group;
        this.zookeeperClient = new ZookeeperClientImpl(this.zk);                       // init zookeeper
        this.zooKeeperServiceDiscovery = new ZooKeeperServiceDiscovery(this.zookeeperClient); // init discovery
        // TODO  init consumer registry

        retryClientThread = new Thread(() -> retryInitClient());
        retryClientThread.setDaemon(true);
        retryClientThread.start();
    }

    public Object invoke(String service, String method, Class<?>[] argTypes, Object[] args)
            throws InterruptedException, FrpcInvokeException {

        NettyClient nettyClient = getExistOrNewOne(service);
        if (!nettyClient.isConnected()) {
            // wait 0.2s
            Thread.sleep(200);
            if (!nettyClient.isConnected()) {
                throw new FrpcInvokeException("No available service found [" + service + "]", null);
            }
        }
        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService(service);
        reqPacket.setMethod(method);
        reqPacket.setArgTypes(argTypes);
        reqPacket.setArgs(args);
        return nettyClient.send(reqPacket);
    }

    private NettyClient getExistOrNewOne(String service) {
        if (clientPool.containsKey(service)) {
            return clientPool.get(service);
        }
        synchronized (this) {
            if (clientPool.containsKey(service)) {
                return clientPool.get(service);
            }
            List<ProviderNode> nodes = this.zooKeeperServiceDiscovery.find(service, this.group);
            if (CollectionUtils.isEmpty(nodes)) {
                String errMsg = "RpcClient service not found\t" + service + "|" + group;
                log.error(errMsg);
                throw new FrpcInvokeException(errMsg, null);
            }

            LoadBalance loadBalance = new RandomLoadBalance();
            ProviderNode node = loadBalance.getOne(nodes);

            NettyClient nettyClient = new NettyClient(
                    node.getService(),
                    node.getAddress().getHost(),
                    node.getAddress().getPort(),
                    () -> closedCallback(service));

            nettyClient.connect(
                    () -> clientPool.put(service, nettyClient),
                    () -> retryClientQueue.add(nettyClient));

            return nettyClient;
        }
    }


    private void closedCallback(String service) {
        log.info("NettyClient closed callback service={}", service);
        // reload service
        zooKeeperServiceDiscovery.reload(service, this.group);
        if (clientPool.containsKey(service)) {
            synchronized (service) {
                if (clientPool.containsKey(service)) {
                    // remove client
                    NettyClient nettyClient = clientPool.remove(service);
                    nettyClient.setConnected(false);
                    retryClientQueue.add(nettyClient);
                }
            }
        }
    }

    private void retryInitClient() {

        while (true) {

            log.info("RpcClient loop retry start ...\n" +
                    "---------\n" +
                    "RpcClient Retry Size:\t" + retryClientQueue.size() +
                    "\n---------");

            while (!retryClientQueue.isEmpty()) {
                NettyClient nettyClient = retryClientQueue.poll();
                String service = nettyClient.getService();
                List<ProviderNode> nodes = this.zooKeeperServiceDiscovery.find(service, this.group);
                if (nodes != null && nodes.size() > 0) {
                    // random balance
                    LoadBalance loadBalance = new RandomLoadBalance();
                    ProviderNode node = loadBalance.getOne(nodes);
                    nettyClient.setHost(node.getAddress().getHost());
                    nettyClient.setPort(node.getAddress().getPort());
                    nettyClient.connect(
                            () -> clientPool.put(service, nettyClient),
                            () -> failedClientQueue.add(nettyClient));
                    continue;
                } else {
                    failedClientQueue.add(nettyClient);
                }

                if (!failedClientQueue.isEmpty()) {
                    retryClientQueue.add(failedClientQueue.poll());
                }

            }

            log.info("RpcClient loop retry end");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // ignore
            }
        }

    }

    public static void main(String[] args ) throws InterruptedException, FrpcZookeeperException {

        String zk = "localhost:2181";
        String group = "test";

        RpcClient client = new RpcClient(zk, group);

        Class<?>[] argTypes = new Class<?>[1];
        argTypes[0] = String.class;
        Object[] argss = new Object[1];
        argss[0] = "Jack";

        while (true) {

            log.info("client.invoke sayHi");

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
