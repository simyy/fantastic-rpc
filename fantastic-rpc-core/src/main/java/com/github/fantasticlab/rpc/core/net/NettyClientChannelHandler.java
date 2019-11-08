package com.github.fantasticlab.rpc.core.net;

import com.github.fantasticlab.rpc.core.exception.FrpcClosedException;
import com.github.fantasticlab.rpc.core.net.protocol.RespPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

@Slf4j
public class NettyClientChannelHandler extends ChannelInboundHandlerAdapter {

    private ConcurrentHashMap<String, SynchronousQueue<Object>> returnObjMap;
    private NettyClient.ClosedCallback closedCallback;

    public NettyClientChannelHandler(ConcurrentHashMap<String, SynchronousQueue<Object>> returnObjMap,
                                     NettyClient.ClosedCallback closedCallback) {
        this.returnObjMap = returnObjMap;
        this.closedCallback = closedCallback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("NettyClient\t" + ctx.channel().remoteAddress() + "\tconnected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("NettyClient\t" + ctx.channel().remoteAddress() + "\tclosed");
        ctx.channel().close();
        closedCallback.run();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RespPacket respPacket = (RespPacket) msg;
        log.info("NettyClient \t" + ctx.channel().remoteAddress()
                + "\tread \n-------------\n"
                + respPacket.toString()
                + "\n-------------");

        SynchronousQueue<Object> queue = returnObjMap.get(respPacket.getInvokeId());
        queue.put(respPacket.getReturnObj());
        returnObjMap.remove(respPacket.getInvokeId());
    }


}
