package com.github.fantasticlab.rpc.core.net;

import com.github.fantasticlab.rpc.core.net.protocol.Packet;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.serialize.JsonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private final EventLoopGroup group = new NioEventLoopGroup();

    private Bootstrap bootstrap;

    private Channel channel;

    private String host;

    private int port;

    public NettyClient(String host, int port) {

        this.host = host;
        this.port = port;
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline cp = channel.pipeline();
                        cp.addLast(new NettyEncoder(new JsonSerializer()));
                        cp.addLast(new NettyDecoder(new JsonSerializer()));
                        cp.addLast(new NettyClientChannelHandler());
                    }
                });
    }

    public void connect() throws InterruptedException {
        ChannelFuture future = bootstrap.connect(host, port).sync();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("NettyClient connect server success!");

                } else {
                    System.out.println("NettyClient connect server faild!");
                    future.cause().printStackTrace();
                    group.shutdownGracefully();
                }

            }
        });
        this.channel = future.sync().channel();
    }

    public void send(Packet packet) {
        this.channel.writeAndFlush(packet);
    }

    public static void main(String[] args) throws InterruptedException {

        NettyClient client = new NettyClient("127.0.0.1", 8080);
        client.connect();

        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService("HelloService");
        reqPacket.setMethod("sayHi");
        client.send(reqPacket);

    }

}
