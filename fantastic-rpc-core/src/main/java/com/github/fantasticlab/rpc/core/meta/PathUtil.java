package com.github.fantasticlab.rpc.core.meta;

public class PathUtil {

    public static String buildPath(BaseNode node) {
        return  "/frpc/" + node.getGroup()
                + "/" + node.getNodeType().tag()
                + "/" + node.getService()
                + "/" + node.getAddress();
    }

    public static String buildProviderPath(String service, String group) {
        return  "/frpc/" + group
                + "/" + NodeType.PROVIDER.tag()
                + "/" + service;
    }

    public static String buildConsumerPath(String service, String group) {
        return  "/frpc/" + group
                + "/" + NodeType.CONSUMER.tag()
                + "/" + service;
    }

}
