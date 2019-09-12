package com.github.fantasticlab.rpc.core.meta;

public enum NodeType {

    PROVIDER(0, "provider"),
    CONSUMER(1, "consumer"),

    ;

    private int code;

    private String tag;

    NodeType(int code, String tag) {
        this.code = code;
        this.tag = tag;
    }

    public int code() {return code;}

    public String tag() {return tag;}
}
