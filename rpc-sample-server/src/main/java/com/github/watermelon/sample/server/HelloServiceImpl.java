package com.github.watermelon.sample.server;

import com.github.watermelon.sample.api.HelloService;
import com.github.watermelon.sample.api.Person;
import com.github.watermelon.server.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
