package com.github.fantasticlab.rpc.core.initializer;

public class ProviderBean<T> {

    private ProviderInitializer initializer;

    private Class<T> clazz;

    public ProviderBean(ProviderInitializer initializer, Class<T> clazz) {
        this.initializer = initializer;
        this.clazz = clazz;
        this.initializer.register(clazz);
    }

}
