package com.github.fantasticlab.rpc.core.meta;

import lombok.Data;

import java.util.List;

@Data
public class ProviderNode extends BaseNode {

    private NodeType nodeType = NodeType.PROVIDER;

//    private List<String> methods;

}
