package com.github.fantasticlab.rpc.core.net.protocol;

import lombok.Data;

import java.util.Random;
import java.util.UUID;

@Data
public abstract class Packet {

    protected String invokeId;

    protected PacketType type;

    abstract public PacketType getType();

    public void generateId() {
        String uuid = UUID.randomUUID().toString();
        setInvokeId(uuid + "-" + new Random().nextInt(1000000) + "|" + Thread.currentThread().getId());
    }
}
