package com.github.fantasticlab.rpc.core.meta;

import lombok.Data;

@Data
public class BaseNode {
    protected NodeType nodeType;
    protected String group;
    protected String service;
    protected Address address;
    protected long registerTime;
    protected long refreshTime;

    public BaseNode(NodeType nodeType, String group, String service, String address) {
        this.nodeType = nodeType;
        this.group = group;
        this.service = service;
        this.address = Address.parse(address);
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeType=" + nodeType +
                ", group='" + group + '\'' +
                ", service='" + service + '\'' +
                ", address=" + address +
                ", registerTime=" + registerTime +
                ", refreshTime=" + refreshTime +
                '}';
    }
}
