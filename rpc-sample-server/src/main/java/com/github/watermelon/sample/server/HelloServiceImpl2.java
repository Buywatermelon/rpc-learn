package com.github.watermelon.sample.server;

import com.github.watermelon.sample.api.HelloService;
import com.github.watermelon.sample.api.Person;
import com.github.watermelon.server.RpcService;

@RpcService(value = HelloService.class, version = "sample.hello2")
public class HelloServiceImpl2 implements HelloService{

    @Override
    public String hello(String name) {
        return "你好！ " + name;
    }

    @Override
    public String hello(Person person) {
        return "你好！ " + person.getFirstName() + " " + person.getLastName();
    }
}
