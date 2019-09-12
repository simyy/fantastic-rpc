package com.github.fantasticlab.rpc.core.net;

import com.github.fantasticlab.rpc.core.context.InvokeRequestContext;
import com.github.fantasticlab.rpc.core.context.InvokeResponseContext;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.net.protocol.RespPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class NettyClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyClient\t" + ctx.channel().remoteAddress() + "\tconnected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyClient\t" + ctx.channel().remoteAddress() + "\tclosed");
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RespPacket respPacket = (RespPacket) msg;
        System.out.println("NettyClient \t" + ctx.channel().remoteAddress()
                + "\tread \n-------------\n"
                + respPacket.toString()
                + "\n-------------");

        // TODO how to return result

//        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("I had recevied (from Java Client)!".getBytes()));
    }
}
