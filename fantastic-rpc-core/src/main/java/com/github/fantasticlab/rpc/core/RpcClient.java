package com.github.fantasticlab.rpc.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fantasticlab.rpc.core.net.NettyClient;
import com.github.fantasticlab.rpc.core.net.protocol.Packet;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.test.HelloService;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class RpcClient  {

    private NettyClient nettyClient;

    public RpcClient(String host, int port) throws InterruptedException {
        NettyClient client = new NettyClient(host, port);
        client.connect();
    }

    public void send(Packet packet) {
        nettyClient.send(packet);
    }

    public static void main(String[] args ) throws InterruptedException {

        RpcClient client = new RpcClient("127.0.0.1", 8080);

        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService(HelloService.class.getSimpleName());
        reqPacket.setMethod("sayHi");
        reqPacket.setArgTypes(null);
        reqPacket.setArgs(null);

        ReqPacket reqPacket1 = new ReqPacket();
        reqPacket1.setService(HelloService.class.getSimpleName());
        reqPacket1.setMethod("sayHi");
        Class<?>[] argTypes = new Class<?>[1];
        argTypes[0] = String.class;
        reqPacket1.setArgTypes(argTypes);
        Object[] argss = new Object[1];
        argss[0] = "Jack";
        reqPacket1.setArgs(argss);

        client.send(reqPacket);
        client.send(reqPacket1);
    }

}
