package com.github.fantasticlab.rpc.core.net.protocol;

import lombok.Data;

import java.util.UUID;

@Data
public class ReqPacket extends Packet {

    private boolean heartbeat = false;

    private String service;

    private String method;

    private Class<?>[] argTypes;

    private Object[] args;

    @Override
    public PacketType getType() {
        return PacketType.Request;
    }

}
