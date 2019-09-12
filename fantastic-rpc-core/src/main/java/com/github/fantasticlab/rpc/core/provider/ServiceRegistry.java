package com.github.fantasticlab.rpc.core.provider;

import com.github.fantasticlab.rpc.core.context.InvokeResponseContext;
import com.github.fantasticlab.rpc.core.exception.InvokeException;
import com.github.fantasticlab.rpc.core.context.InvokeRequestContext;

public interface ServiceRegistry {

    void register(Class<?> clazz);

    InvokeResponseContext invoke(InvokeRequestContext context) throws InvokeException;

}
