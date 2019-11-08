package com.github.fantasticlab.rpc.core.meta;

import lombok.Data;

@Data
public class Address {

    private String host;
    private Integer port;

    public Address(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    public static Address parse(String address) {
        String[] items = address.split(":");
        return new Address(items[0], Integer.valueOf(items[1]));
    }
}
