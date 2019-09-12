package com.github.fantasticlab.rpc.core.net.protocol;

import lombok.Data;

@Data
public class RespPacket extends Packet {

    private String service;

    private String method;

    private Class<?>[] argTypes;

    private Object[] args;

    private Object returnObj;

    @Override
    public PacketType getType() {
        return PacketType.Response;
    }

}
