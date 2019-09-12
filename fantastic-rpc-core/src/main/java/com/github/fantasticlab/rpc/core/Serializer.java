package com.github.fantasticlab.rpc.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fantasticlab.rpc.core.net.protocol.Packet;
import com.github.fantasticlab.rpc.core.serialize.SerializerType;

import java.io.IOException;

public interface Serializer {

    SerializerType getType();

    byte[] serialize(Packet packet) throws JsonProcessingException;

    Packet deserialize(byte[] bytes, Class<? extends Packet> clazz) throws IOException;

}
