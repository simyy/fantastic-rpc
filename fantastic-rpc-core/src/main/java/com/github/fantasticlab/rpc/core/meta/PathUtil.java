package com.github.fantasticlab.rpc.core.meta;

public class PathUtil {

    public static String buildPath(BaseNode node) {
        return  "/frpc/" + node.getGroup()
                + "/" + node.getNodeType().tag()
                + "/" + node.getService()
                + "/" + node.getAddress();
    }

}
