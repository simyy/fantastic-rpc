package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.RpcClient;
import com.github.fantasticlab.rpc.core.RpcServer;
import com.github.fantasticlab.rpc.core.example.HelloService;
import com.github.fantasticlab.rpc.core.example.HelloServiceImpl;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RpcInvokeTest {

    @Before
    public void setUp() throws Exception {
        String zk = "localhost:2181";
        Integer port = 8080;
        String group = "jdkProxyTest";

        RpcServer rpcServer = new RpcServer(zk, port, group);
        rpcServer.register(HelloServiceImpl.class);
    }

    @Test
    public void test() throws FrpcZookeeperException, InterruptedException {

        String zkAddress = "localhost:2181";
        String group = "jdkProxyTest";

        RpcClient client = new RpcClient(zkAddress, group);

        Class<?>[] argTypes = new Class<?>[1];
        argTypes[0] = String.class;
        Object[] args = new Object[1];
        args[0] = "Jack";

        String rs1 = (String) client.invoke(HelloService.class.getSimpleName(), "sayHi", null, null);
        Assert.assertTrue(rs1 != null && "Hi!!!".equals(rs1));

        String rs2 = (String) client.invoke(HelloService.class.getSimpleName(), "sayHi", argTypes, args);
        Assert.assertTrue(rs2 != null && "Hi!!! Jack".equals(rs2));

    }

}
