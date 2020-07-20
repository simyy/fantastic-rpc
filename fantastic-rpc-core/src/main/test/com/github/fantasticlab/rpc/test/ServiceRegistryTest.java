package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.RpcServer;
import com.github.fantasticlab.rpc.core.context.InvokeRequestContext;
import com.github.fantasticlab.rpc.core.context.InvokeResponseContext;
import com.github.fantasticlab.rpc.core.example.HelloServiceImpl;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistry;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistryImpl;
import com.github.fantasticlab.rpc.core.registry.ProviderRegistry;
import com.github.fantasticlab.rpc.core.registry.ZookeeperProviderRegistry;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZookeeperClientImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServiceRegistryTest {

    private ZookeeperClient zookeeperClient;
    private ProviderRegistry providerRegistry;
    private ServiceRegistry serviceRegistry;

    private String service = "HelloService";
    private String method = "sayHi";
    private String group = "providerInitTest";
    private Integer port = 8080;


    @Before
    public void setUp() throws Exception {
        zookeeperClient = new ZookeeperClientImpl("localhost:2181");
        providerRegistry = new ZookeeperProviderRegistry(zookeeperClient);
    }

    @Test
    public void test() throws FrpcZookeeperException {

        serviceRegistry = new ServiceRegistryImpl(group, port, providerRegistry);
        serviceRegistry.register(HelloServiceImpl.class);

        InvokeRequestContext context = new InvokeRequestContext();
        context.setService(service);
        context.setMethod(method);
        context.setArgTypes(null);
        context.setArgs(null);
        InvokeResponseContext rs1 = serviceRegistry.invoke(context);
        Assert.assertTrue(rs1 != null && "Hi!!!".equals(rs1.getResult()));

        InvokeRequestContext context2 = new InvokeRequestContext();
        context2.setService(service);
        context2.setMethod(method);
        Class<?>[] argTypes = new Class<?>[1];
        argTypes[0] = String.class;
        context2.setArgTypes(argTypes);
        Object[] argss = new Object[1];
        argss[0] = "Jack";
        context2.setArgs(argss);
        InvokeResponseContext rs2 =serviceRegistry.invoke(context2);
        Assert.assertTrue(rs2 != null && "Hi!!! Jack".equals(rs2.getResult()));
    }
}
