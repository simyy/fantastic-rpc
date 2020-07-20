package com.github.fantasticlab.rpc.core.example;

import com.github.fantasticlab.rpc.core.annotation.Frpc;

@Frpc(value = "HelloService")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHi() {
        return "Hi!!!";
    }

    @Override
    public String sayHi(String name) {
        return "Hi!!! " + name;
    }

}
