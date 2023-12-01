package com.ooooo.protocol.dubbo.service;

import com.ooooo.protocol.core.annotation.APIService;
import com.ooooo.protocol.dubbo.request.DubboMapping;

import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@APIService
public interface DemoService {

    @DubboMapping(value = "com.ooooo.protocol.dubbo.service.HelloService.say")
    String test(Map<String, Object> request);

}
