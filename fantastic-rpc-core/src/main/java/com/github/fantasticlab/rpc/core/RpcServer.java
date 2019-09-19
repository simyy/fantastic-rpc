package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.annotation.FrpcLoader;
import com.github.fantasticlab.rpc.core.net.NettyServer;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistry;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistryImpl;
import com.github.fantasticlab.rpc.core.registry.ProviderRegistry;
import com.github.fantasticlab.rpc.core.registry.ZKProviderRegistry;
import com.github.fantasticlab.rpc.core.test.HelloService;
import com.github.fantasticlab.rpc.core.test.HelloServiceImpl;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;
import org.springframework.beans.factory.xml.XmlBeanFactory;

/**
 * Hello world!
 *
 */
//@ImportResource(value = "classpath:*ApplicationContext.xml")
public class RpcServer {

    public static void main( String[] args ) throws InterruptedException {
        System.out.println( "RpcServer starting ......\n" );

        // init zookeeper
        ZkClient zkClient = new ZkClientImpl();
        // init provider registry
        ProviderRegistry providerRegistry = new ZKProviderRegistry(zkClient);
        // init service registry
        ServiceRegistry serviceRegistry = new ServiceRegistryImpl("test", providerRegistry);
        // registry HelloService
        serviceRegistry.register(HelloServiceImpl.class);
//        XmlWebApplicationContext ctx = new XmlWebApplicationContext();
//        FrpcLoader frpcLoader = new FrpcLoader(serviceRegistry, xbf);
//        frpcLoader.scan();

        NettyServer server = new NettyServer(8080, serviceRegistry);
        server.start();

    }
}
