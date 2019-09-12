package com.github.fantasticlab.rpc.core.net.protocol;

import lombok.Data;

@Data
public abstract class Packet {

    protected PacketType type;

    abstract public PacketType getType();
}
