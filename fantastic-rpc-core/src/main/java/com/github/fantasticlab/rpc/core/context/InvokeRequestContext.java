package com.github.fantasticlab.rpc.core.context;

import lombok.Data;

@Data
public class InvokeRequestContext {

    private String service;

    private String method;

//    private Class<?> resultType;

    private Class<?>[] argTypes;

    private Object[] args;

}
