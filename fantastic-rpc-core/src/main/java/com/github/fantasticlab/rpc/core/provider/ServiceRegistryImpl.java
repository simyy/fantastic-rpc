package com.github.fantasticlab.rpc.core.provider;

import com.github.fantasticlab.rpc.core.context.InvokeResponseContext;
import com.github.fantasticlab.rpc.core.exception.InvokeException;
import com.github.fantasticlab.rpc.core.exception.RegistryException;
import com.github.fantasticlab.rpc.core.context.InvokeRequestContext;
import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import com.github.fantasticlab.rpc.core.registry.ProviderRegistry;
import com.github.fantasticlab.rpc.core.registry.ZKProviderRegistry;
import com.github.fantasticlab.rpc.core.util.NetUtils;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClient;
import com.github.fantasticlab.rpc.core.zookeeper.ZkClientImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistryImpl implements ServiceRegistry {

    private String group;

    private ProviderRegistry providerRegistry;

    private Map<String, ServiceNode> serviceMap = new ConcurrentHashMap<>();

    public ServiceRegistryImpl(String group,
                               ProviderRegistry providerRegistry) {
        this.group = group;
        this.providerRegistry = providerRegistry;
    }

    @Data
    @AllArgsConstructor
    private class ServiceNode {
        private Object obj;
        private Class<?> clazz;
    }

    @Override
    public void register(Class<?> clazz) {

        try {
            Object service = clazz.newInstance();
            ServiceNode serviceNode = new ServiceNode(service, clazz);
            serviceMap.put(clazz.getSimpleName(), serviceNode);

            ProviderNode providerNode = new ProviderNode();
            providerNode.setService(clazz.getSimpleName());
            providerNode.setGroup(this.group);
            providerNode.setAddress(NetUtils.getLocalIp() + ":" + NetUtils.getLocalUnusedPort());
            long now = new Date().getTime();
            providerNode.setRegisterTime(now);
            providerNode.setRefreshTime(now);
            providerRegistry.register(providerNode);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RegistryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InvokeResponseContext invoke(InvokeRequestContext context) throws InvokeException {
        if (!serviceMap.containsKey(context.getService())) {
            throw new InvokeException();
        }

        ServiceNode serviceNode = serviceMap.get(context.getService());
        try {
            Method method = serviceNode.getClazz().getDeclaredMethod(
                    context.getMethod(), context.getArgTypes());
            Object result =  method.invoke(serviceNode.getObj(), context.getArgs());
            return new InvokeResponseContext(context, result);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class HelloService {

        public void sayHi() {
            System.out.println("Hi!!!");
        }

        public void sayHi(String name) {
            System.out.println("Hi!!! " + name);
        }

    }

    public static void main(String[] args) throws InvokeException {

        ZkClient zkClient = new ZkClientImpl();
        ProviderRegistry providerRegistry = new ZKProviderRegistry(zkClient);

        ServiceRegistry serviceRegistry = new ServiceRegistryImpl("test", providerRegistry);

        serviceRegistry.register(HelloService.class);

        InvokeRequestContext context = new InvokeRequestContext();
        context.setService("HelloService");
        context.setMethod("sayHi");
        context.setArgTypes(null);
        context.setArgs(null);
        serviceRegistry.invoke(context);

        InvokeRequestContext context2 = new InvokeRequestContext();
        context2.setService("HelloService");
        context2.setMethod("sayHi");
        Class<?>[] argTypes = new Class<?>[1];
        argTypes[0] = String.class;
        context2.setArgTypes(argTypes);
        Object[] argss = new Object[1];
        argss[0] = "Jack";
        context2.setArgs(argss);
        serviceRegistry.invoke(context2);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
