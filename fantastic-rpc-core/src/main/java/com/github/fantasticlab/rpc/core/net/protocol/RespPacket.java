package com.github.fantasticlab.rpc.core.net.protocol;

import lombok.Data;

@Data
public class RespPacket extends Packet {

    @Override
    public PacketType getType() {
        return PacketType.Request;
    }

}
