package com.github.fantasticlab.rpc.core.util;

import com.github.fantasticlab.rpc.core.meta.BaseNode;

public class ClientUtils {

    public static String buildClientKey(String service, String group) {
        return service + "/" + group;
    }

    public static String buildClientKey(BaseNode baseNode) {
        return buildClientKey(baseNode.getService(), baseNode.getGroup());
    }

}
