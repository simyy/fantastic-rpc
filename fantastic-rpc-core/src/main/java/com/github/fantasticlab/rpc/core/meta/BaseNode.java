package com.github.fantasticlab.rpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public abstract class BaseNode {

    protected NodeType nodeType;

    protected String nodeKey;

//    protected String region = "default";

    protected String group = "default";

    protected String service;

    protected Address address;

    protected long registerTime;

    protected long refreshTime;

    @Data
    public static class Address {
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
            return new BaseNode.Address(items[0], Integer.valueOf(items[1]));
        }
    }

}
