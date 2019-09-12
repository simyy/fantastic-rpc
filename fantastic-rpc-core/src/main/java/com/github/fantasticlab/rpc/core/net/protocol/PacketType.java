package com.github.fantasticlab.rpc.core.net.protocol;

public enum  PacketType {

    Request(0, ReqPacket.class),
    Response(1, RespPacket.class)

    ;

    int code;

    Class<? extends Packet> clazz;

    PacketType(int code, Class<? extends Packet> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    int code() {
        return code;
    }

    Class<? extends Packet> clazz() {
        return clazz;
    }

    public static PacketType getByCode(int code) {
        for (PacketType value : PacketType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
