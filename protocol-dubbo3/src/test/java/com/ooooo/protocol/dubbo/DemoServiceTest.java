package com.ooooo.protocol.dubbo;

import com.ooooo.protocol.dubbo.service.DemoService;
import com.ooooo.protocol.dubbo.service.HelloService;
import com.ooooo.protocol.dubbo.service.HelloServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest(classes = DubboProtocolTestConfiguration.class)
public class DemoServiceTest {

    @Autowired
    private DemoService demoService;

    @BeforeAll
    static void startDubboServer() {
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(HelloService.class);
        serviceConfig.setRef(new HelloServiceImpl());
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("nacos://127.0.0.1:8848");
        registryConfig.setGroup("DUBBO_SERVICE_GROUP");
        registryConfig.setParameters(Collections.singletonMap("namespace", "test"));
        DubboBootstrap dubboBootstrap = DubboBootstrap.getInstance()
                .application("demo")
                .protocol(new ProtocolConfig("dubbo"))
                .service(serviceConfig)
                .registry(registryConfig);
        dubboBootstrap.start();
    }

    @Test
    void test() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "123");
        request.put("message", "456");

        String result = demoService.test(request);
        Assertions.assertThat(result).isEqualTo("123456");
    }



}
