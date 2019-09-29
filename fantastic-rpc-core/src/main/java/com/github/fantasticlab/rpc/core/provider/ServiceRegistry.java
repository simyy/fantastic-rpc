package com.github.fantasticlab.rpc.core.provider;

import com.github.fantasticlab.rpc.core.context.InvokeResponseContext;
import com.github.fantasticlab.rpc.core.exception.FrpcRegistryException;
import com.github.fantasticlab.rpc.core.exception.FrpcInvokeException;
import com.github.fantasticlab.rpc.core.context.InvokeRequestContext;

public interface ServiceRegistry {

    void register(Class<?> clazz) throws FrpcRegistryException;

    InvokeResponseContext invoke(InvokeRequestContext context) throws FrpcInvokeException;

}
