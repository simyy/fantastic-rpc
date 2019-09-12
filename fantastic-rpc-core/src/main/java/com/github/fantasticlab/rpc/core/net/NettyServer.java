package com.github.fantasticlab.rpc.core.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyServer{

    private static final EventLoopGroup masterGroup = new NioEventLoopGroup(1);

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    private int port;


    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {

        InetSocketAddress address = new InetSocketAddress(port);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(masterGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyServerChannelHandler());
                    }
                });

        ChannelFuture future = bootstrap.bind(address).sync();
        future.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("NettyServer bind [" + this.port  + "] success!");
            } else {
                System.out.println("NettyServer bind [" + this.port  + "] fail!");
                future.cause().printStackTrace();
                masterGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });

    }

    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer(8080);
        server.start();
    }
}
