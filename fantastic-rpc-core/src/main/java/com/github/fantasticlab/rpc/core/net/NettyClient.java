package com.github.fantasticlab.rpc.core.net;

import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.net.protocol.Packet;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.serialize.JsonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NettyClient {

    private final EventLoopGroup group = new NioEventLoopGroup();

    private Bootstrap bootstrap;

    private Channel channel;

    private String host;

    private int port;

    private String service;

    private AtomicBoolean connected = new AtomicBoolean(false);

    private Thread heartbeatThread;

    private ClosedCallback closedCallback;

    private ConcurrentHashMap<String, SynchronousQueue<Object>> returnObjMap = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface ClosedCallback {
        void run();
    }

    @FunctionalInterface
    public interface ConnectSuccessCallback {
        void run();
    }

    @FunctionalInterface
    public interface ConnectFailedCallback {
        void run();
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    public NettyClient(String service, String host, int port, ClosedCallback closedCallback) {
        this.service = service;
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
                        cp.addLast(new NettyClientChannelHandler(returnObjMap, closedCallback));
                    }
                });
    }

    public void connect(ConnectSuccessCallback successCallback, ConnectFailedCallback failedCallback) {
//        ChannelFuture future = bootstrap.connect(host, port).sync();
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    setConnected(true);
                    log.info("NettyClient connect server success!");
                    if (successCallback != null) {
                        successCallback.run();
                    }
                } else {
                    future.cause().printStackTrace();
                    group.shutdownGracefully();
                    if (failedCallback != null) {
                        failedCallback.run();
                    }
                    setConnected(false);
                    log.error("NettyClient connect server faild!");
                }

            }
        });
//        this.channel = future.sync().channel();
        this.channel = future.channel();

        this.heartbeatThread = new Thread(this::heartbeat);
        this.heartbeatThread.setDaemon(true);
        this.heartbeatThread.start();

    }

    public Object send(ReqPacket packet) throws InterruptedException {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        packet.generateId();
        returnObjMap.put(packet.getInvokeId(), queue);
        this.channel.writeAndFlush(packet);
        log.info("NettyClient send start ...");
        Object rs = queue.take();
        log.info("NettyClient send->recv {}", rs);
        return rs;
    }

    public void heartbeat() {

        while (connected.get()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignore) {
            }

            ReqPacket packet = new ReqPacket();
            packet.setHeartbeat(true);

            ChannelFuture future = this.channel.writeAndFlush(packet);
            if (future.isSuccess()) {
                // pass
            } else {
                closedCallback.run();
                log.info("NettyClient heartbeat failed");
                return;
            }
            log.info("NettyClient heartbeat ...");
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public static void main(String[] args) throws InterruptedException {

        NettyClient client = new NettyClient("HelloService", "127.0.0.1", 8080, null);
        client.connect(null, null);

        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService("HelloService");
        reqPacket.setMethod("sayHi");
        client.send(reqPacket);

    }

}
