package com.github.fantasticlab.rpc.core.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fantasticlab.rpc.core.net.protocol.Packet;

import java.io.IOException;

public class JsonSerializer implements Serializer {

    @Override
    public SerializerType getType() {
        return SerializerType.JSON;
    }

    @Override
    public byte[] serialize(Packet packet) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(packet);
    }

    @Override
    public Packet deserialize(byte[] bytes, Class<? extends Packet> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(bytes, clazz);
    }


}
