package com.github.fantasticlab.rpc.core.zookeeper;

import org.apache.commons.lang.StringUtils;

public class PathUtil {

    public static String buildPath(String... args) {
        return StringUtils.join(args, "/");
    }

    public static String parsePrePath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

}
