package com.github.fantasticlab.rpc.test;

import com.github.fantasticlab.rpc.core.net.protocol.ReqPacket;
import com.github.fantasticlab.rpc.core.serialize.JsonSerializer;
import com.github.fantasticlab.rpc.core.serialize.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class JsonSerializerTest {

    @Test
    public void test() throws IOException {

        String service = "HelloService";
        String method = "sayHi";

        ReqPacket reqPacket = new ReqPacket();
        reqPacket.setService(service);
        reqPacket.setMethod(method);

        Serializer serializer = new JsonSerializer();
        byte[] encode = serializer.serialize(reqPacket);
        ReqPacket decode = (ReqPacket) serializer.deserialize(encode, ReqPacket.class);
        Assert.assertTrue(decode.getMethod().equals(method) && decode.getService().equals(service));
    }

}
