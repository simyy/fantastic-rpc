package com.github.fantasticlab.rpc.core.context;

import lombok.Data;

@Data
public class InvokeResponseContext {

    private String service;

    private String method;

//    private Class<?> resultType;

    private Object result;

    public InvokeResponseContext(InvokeRequestContext context, Object result) {
        this.service = context.getService();
        this.method = context.getMethod();
//        this.resultType = context.getResultType();
        this.result = result;
    }

}
