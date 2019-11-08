package com.github.fantasticlab.rpc.core.util;

import com.github.fantasticlab.rpc.core.meta.BaseNode;
import com.github.fantasticlab.rpc.core.meta.NodeType;

public class PoolUtil {

    private static final String PREFIX = "/frpc";

    public static String buildPath(BaseNode node) {
        return  PREFIX + node.getGroup()
                + "/" + node.getNodeType().tag()
                + "/" + node.getService()
                + "/" + node.getAddress();
    }

    public static String buildKey(String service, String group) {
        return service + "/" + group;
    }

}
