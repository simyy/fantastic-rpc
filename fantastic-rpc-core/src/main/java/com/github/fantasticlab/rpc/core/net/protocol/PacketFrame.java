package com.github.fantasticlab.rpc.core.net.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fantasticlab.rpc.core.Serializer;
import com.github.fantasticlab.rpc.core.serialize.JsonSerializer;
import com.github.fantasticlab.rpc.core.serialize.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.io.IOException;

public class PacketFrame {

    public static final int MAGIC_NUMBER = 0x860860;

    @Deprecated
    public static ByteBuf encode(ByteBufAllocator byteBufAllocator, Serializer serializer, Packet packet)
            throws JsonProcessingException {
        ByteBuf byteBuf = byteBufAllocator.ioBuffer();
        byte[] bytes = serializer.serialize(packet);
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeInt(serializer.getType().code());
        byteBuf.writeInt(packet.getType().code);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    @Deprecated
    public static Packet decode(ByteBuf byteBuf) throws IllegalAccessException, InstantiationException, IOException {
        byteBuf.readInt(); // 跳过MAGIC_NUMBER
        int serializeTypeCode = byteBuf.readInt(); // 序列化类型
        int packetTypeCode = byteBuf.readInt(); // 数据类型
        int packetLength = byteBuf.readInt(); // 数据包长度
        byte[] bytes = new byte[packetLength];
        byteBuf.readBytes(bytes);

        SerializerType serializerType = SerializerType.getByCode(serializeTypeCode);
        PacketType packetType = PacketType.getByCode(packetTypeCode);

        Serializer serializer = serializerType.clazz().newInstance();
        return serializer.deserialize(bytes, packetType.clazz);
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {

        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService("HelloService");
        reqPacket.setMethod("sayHi");

        ByteBufAllocator byteBufAllocator = new UnpooledByteBufAllocator(false);
        ByteBuf encode = PacketFrame.encode(byteBufAllocator, new JsonSerializer(), reqPacket);
        ReqPacket decode = (ReqPacket) PacketFrame.decode(encode);
        assert decode != null;

    }
}
