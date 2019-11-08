package com.github.fantasticlab.rpc.core.meta;

import lombok.Data;

@Data
public class ProviderNode extends BaseNode {

    public ProviderNode(String group, String service, String address) {
        super(NodeType.PROVIDER, group, service, address);
    }

    @Override
    public String toString() {
        return "ProviderNode{" +
                ", group='" + group + '\'' +
                ", service='" + service + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
