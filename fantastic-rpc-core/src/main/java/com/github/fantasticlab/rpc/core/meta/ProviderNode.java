package com.github.fantasticlab.rpc.core.meta;

import lombok.Data;

@Data
public class ProviderNode extends BaseNode {

    private NodeType nodeType = NodeType.PROVIDER;

//    private List<String> methods;


    @Override
    public String toString() {
        return "ProviderNode{" +
                "nodeType=" + nodeType +
                ", nodeType=" + nodeType +
                ", nodeKey='" + nodeKey + '\'' +
                ", group='" + group + '\'' +
                ", service='" + service + '\'' +
                ", address='" + address + '\'' +
                ", registerTime=" + registerTime +
                ", refreshTime=" + refreshTime +
                '}';
    }
}
