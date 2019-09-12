package com.github.fantasticlab.rpc.core.net;

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
        System.out.println("NettyClient ["
                + ctx.channel().remoteAddress()
                + "] connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyClient ["
                + ctx.channel().remoteAddress()
                + "] closed");
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("NettyClient ["
                + ctx.channel().remoteAddress()
                + "] read \n-------------\n"
                + byteBuf.toString(Charset.defaultCharset())
                + "\n-------------");
//        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("I had recevied (from Java Client)!".getBytes()));
    }
}
