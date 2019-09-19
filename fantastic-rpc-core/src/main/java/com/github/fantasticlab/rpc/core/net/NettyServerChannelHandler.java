package com.github.fantasticlab.rpc.core.net;

import com.github.fantasticlab.rpc.core.context.InvokeRequestContext;
import com.github.fantasticlab.rpc.core.context.InvokeResponseContext;
import com.github.fantasticlab.rpc.core.net.protocol.PacketType;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.net.protocol.RespPacket;
import com.github.fantasticlab.rpc.core.provider.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;


@Slf4j
public class NettyServerChannelHandler extends ChannelInboundHandlerAdapter {

    private ServiceRegistry serviceRegistry;

    public NettyServerChannelHandler(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyServer\t"
                + ctx.channel().remoteAddress()
                + "\tconnected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyServer\t"
                + ctx.channel().remoteAddress()
                + "\tclosed");
        ctx.channel().close();
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("NettyServer\t"
//                + ctx.channel().remoteAddress()
//                + "\tread complete");
//        ctx.flush();
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ReqPacket reqPacket = (ReqPacket) msg;
        System.out.println("NettyServer\t"
                + ctx.channel().remoteAddress()
                + "\tread \n-------------\n"
                + msg.toString()
                + "\n-------------");

        if (serviceRegistry == null) {
            RespPacket respPacket = new RespPacket();
            BeanUtils.copyProperties(reqPacket, respPacket);
            respPacket.setType(PacketType.Response);
            respPacket.setReturnObj("I's OK (By NettyServer)");
            ctx.channel().writeAndFlush(respPacket);
            return;
        }

        InvokeRequestContext context = new InvokeRequestContext();
        context.setService(reqPacket.getService());
        context.setMethod(reqPacket.getMethod());
        context.setArgTypes(reqPacket.getArgTypes());
        context.setArgs(reqPacket.getArgs());
        InvokeResponseContext responseContext = serviceRegistry.invoke(context);

        RespPacket respPacket = new RespPacket();
        BeanUtils.copyProperties(reqPacket, respPacket);
        respPacket.setType(PacketType.Response);
        respPacket.setReturnObj(responseContext.getResult());

        ctx.channel().writeAndFlush(respPacket);
    }
}
