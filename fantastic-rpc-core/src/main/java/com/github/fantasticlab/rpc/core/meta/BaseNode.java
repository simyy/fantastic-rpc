package com.github.fantasticlab.rpc.core.meta;

import lombok.Data;

@Data
public abstract class BaseNode {

    protected NodeType nodeType;

    protected String nodeKey;

//    protected String region = "default";

    protected String group = "default";

    protected String service;

    protected String address;

    protected long registerTime;

    protected long refreshTime;

}
