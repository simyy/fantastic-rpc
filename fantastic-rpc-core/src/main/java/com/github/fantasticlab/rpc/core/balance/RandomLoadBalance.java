package com.github.fantasticlab.rpc.core.balance;

import com.github.fantasticlab.rpc.core.meta.ProviderNode;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {

    @Override
    public ProviderNode getOne(List<ProviderNode> nodes) {

        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }

        return nodes.get(new Random().nextInt(nodes.size()));
    }
}
