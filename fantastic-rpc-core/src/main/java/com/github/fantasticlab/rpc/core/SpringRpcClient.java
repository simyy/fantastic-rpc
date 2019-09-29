package com.github.fantasticlab.rpc.core;

import com.github.fantasticlab.rpc.core.net.NettyClient;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class SpringRpcClient implements InvocationHandler {

    private NettyClient client;

    public SpringRpcClient(NettyClient client) {
        this.client = client;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService(method.getDeclaringClass().getName());
        reqPacket.setMethod(method.getName());
        reqPacket.setArgTypes(method.getParameterTypes());
        reqPacket.setArgs(objects);
        // TODO traceId
        client.send(reqPacket);

        return null;
    }


}
