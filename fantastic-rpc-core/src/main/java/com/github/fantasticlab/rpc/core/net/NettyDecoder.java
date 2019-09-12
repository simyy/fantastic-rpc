package com.github.fantasticlab.rpc.core.net;

import com.github.fantasticlab.rpc.core.Serializer;
import com.github.fantasticlab.rpc.core.context.InvokeRequestContext;
import com.github.fantasticlab.rpc.core.context.InvokeResponseContext;
import com.github.fantasticlab.rpc.core.net.protocol.PacketFrame;
import com.github.fantasticlab.rpc.core.net.protocol.PacketType;
import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.net.protocol.RespPacket;
import com.github.fantasticlab.rpc.core.serialize.JsonSerializer;
import com.github.fantasticlab.rpc.core.serialize.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.beans.BeanUtils;

import java.util.List;


public class NettyDecoder extends ByteToMessageDecoder {

    private Serializer serializer;

    public NettyDecoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        System.out.println(in);

        int index;
        while (true) {
            index = in.readerIndex();
            in.markReaderIndex();
            // check magic number
            if (in.readInt() == PacketFrame.MAGIC_NUMBER) {
                System.out.println("Decode\tfind magic number index=" + index);
                break;
            }
            in.resetReaderIndex();
            in.readByte();
            // magic number + serialType + packType + dataLength
            if (in.readableBytes() < 4 + 4 + 4 + 4) {
                return;
            }
        }

        int serialType = in.readInt();
        int packType = in.readInt();
        int length = in.readInt();
        System.out.println("Decode\t" + serialType + "\t" + packType + "\t" + length);
        if (in.readableBytes() < length) {
            // not enough then rollback
            in.readerIndex(index);
            return;
        }

        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        SerializerType serializerType = SerializerType.getByCode(serialType);
        PacketType packetType = PacketType.getByCode(packType);

        out.add(serializer.deserialize(bytes, packetType.clazz()));
    }
}
