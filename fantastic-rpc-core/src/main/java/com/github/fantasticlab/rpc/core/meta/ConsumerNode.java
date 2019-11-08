package com.github.fantasticlab.rpc.core.meta;

import lombok.Data;

public class ConsumerNode extends BaseNode {

    public ConsumerNode(String group, String service, String address) {
        super(NodeType.CONSUMER, group, service, address);
    }

    @Override
    public String toString() {
        return "ConsumerNode{" +
                ", group='" + group + '\'' +
                ", service='" + service + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
