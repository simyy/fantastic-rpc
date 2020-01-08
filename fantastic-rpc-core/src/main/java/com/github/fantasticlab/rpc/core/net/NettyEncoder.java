package com.github.fantasticlab.rpc.core.net;

import com.github.fantasticlab.rpc.core.serialize.Serializer;
import com.github.fantasticlab.rpc.core.net.protocol.Packet;
import com.github.fantasticlab.rpc.core.net.protocol.PacketFrame;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<Packet> {

    private Serializer serializer;

    public NettyEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(PacketFrame.MAGIC_NUMBER);
        out.writeInt(serializer.getType().code());
        out.writeInt(msg.getType().code());
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
