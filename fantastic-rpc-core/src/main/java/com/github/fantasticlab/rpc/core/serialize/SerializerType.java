package com.github.fantasticlab.rpc.core.serialize;

import com.github.fantasticlab.rpc.core.Serializer;

public enum SerializerType {

    JSON(0, JsonSerializer.class),

    ;

    int code;

    Class<? extends Serializer> clazz;

    SerializerType(int code, Class<? extends Serializer> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public int code() {
        return code;
    }

    public Class<? extends Serializer> clazz() {
        return clazz;
    }

    public static SerializerType getByCode(int code) {
        for (SerializerType value : SerializerType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
