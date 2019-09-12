package com.github.fantasticlab.rpc.core.net.protocol;

import com.github.fantasticlab.rpc.core.Serializer;
import com.github.fantasticlab.rpc.core.serialize.SerializerType;
import lombok.Data;

@Data
public abstract class Packet {

    protected PacketType type;

    abstract public PacketType getType();
}
