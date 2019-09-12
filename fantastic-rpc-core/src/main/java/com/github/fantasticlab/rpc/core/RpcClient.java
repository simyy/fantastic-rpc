package com.github.fantasticlab.rpc.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fantasticlab.rpc.core.net.NettyClient;
import com.github.fantasticlab.rpc.core.net.protocol.PacketFrame;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistryImpl;
import com.github.fantasticlab.rpc.core.serialize.JsonSerializer;
import io.netty.buffer.UnpooledByteBufAllocator;

public class RpcClient {

    public static void main( String[] args ) throws InterruptedException, JsonProcessingException {

        NettyClient client = new NettyClient("127.0.0.1", 8080);
        client.connect();

        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService(ServiceRegistryImpl.HelloService.class.getSimpleName());
        reqPacket.setMethod("sayHi");
        reqPacket.setArgTypes(null);
        reqPacket.setArgs(null);

        ReqPacket reqPacket1 = new ReqPacket();
        reqPacket1.setService(ServiceRegistryImpl.HelloService.class.getSimpleName());
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
