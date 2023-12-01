package com.ooooo.protocol.dubbo.service;

public class HelloServiceImpl implements HelloService {

    @Override
    public String say(Message message) {
        return message.getName() + message.getMessage();
    }
}