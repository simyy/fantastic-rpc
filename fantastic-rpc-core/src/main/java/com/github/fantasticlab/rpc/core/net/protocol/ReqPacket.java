package com.github.fantasticlab.rpc.core.net.protocol;

import lombok.Data;

@Data
public class ReqPacket extends Packet {

    private String service;

    private String method;

    @Override
    public PacketType getType() {
        return PacketType.Request;
    }
}
