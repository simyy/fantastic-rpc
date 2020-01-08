package com.github.fantasticlab.rpc.core.proxy;

import com.github.fantasticlab.rpc.core.RpcClient;
import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

abstract class AbstractProxyFactory implements ProxyFactory {

    private RpcClient rpcClient;

    protected String zk;

    protected String group;

    protected MInvocationHandler invocationHandler = new MInvocationHandler();

    public AbstractProxyFactory(String zk, String group) throws FrpcZookeeperException, InterruptedException {
        this.zk = zk;
        this.group = group;
        this.rpcClient = new RpcClient(zk, group);
    }

    @Override
    abstract public  <T> T getProxy(Class<T> clz);

    protected class MInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return rpcClient.invoke(
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    method.getParameterTypes(),
                    args);
        }
    }
}
