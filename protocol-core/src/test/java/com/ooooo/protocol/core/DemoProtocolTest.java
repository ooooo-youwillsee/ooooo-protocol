package com.ooooo.protocol.core;

import com.ooooo.protocol.core.annotation.IAPIService;
import com.ooooo.protocol.core.bootstrap.APIServiceProtocolPropertiesRefresher;
import com.ooooo.protocol.core.context.APIServiceConfig;
import com.ooooo.protocol.core.request.AProtocolWrapper;
import com.ooooo.protocol.core.request.BProtocolWrapper;
import com.ooooo.protocol.core.service.HelloService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@SpringBootTest(classes = CoreProtocolTestConfiguration.class)
public class DemoProtocolTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AbstractEnvironment environment;

    @Autowired
    private APIServiceProtocolPropertiesRefresher refresher;

    @Test
    public void testApiService() {
        HelloService bean = context.getBean(HelloService.class);
        assertEquals("123", bean.invokeNormalMethod("123"));
        IAPIService service = (IAPIService) bean;
        APIServiceConfig apiServiceConfig = service.getAPIServiceConfig();
        assertEquals("demo", apiServiceConfig.getProtocolId());
    }

    @Test
    public void testApiServiceWithDefaultMethod() {
        HelloService bean = context.getBean(HelloService.class);

        assertEquals("123123", bean.invokeDefaultMethod("123"));
        assertEquals("123", bean.invokeDefaultMethodForNormalMethod("123"));
    }

    @Test
    @SneakyThrows
    public void testProtocolScanRefresh() {
        HelloService bean = context.getBean(HelloService.class);
        assertEquals("b", bean.invokerRefreshValue());

        // test refresh config
        {
            TestPropertyValues.of("ooooo.protocol.demo.config.a = refresh-b").applyTo(environment);
            refresher.refreshProtocolBean();

            assertEquals("refresh-b", bean.invokerRefreshValue());
        }

        // test refresh protocol-class
        {
            TestPropertyValues.of("ooooo.protocol.demo.protocol-class = com.ooooo.protocol.core.request" +
                    ".DemoProtocol2").applyTo(environment);
            refresher.refreshProtocolBean();

            assertEquals("DemoProtocol2", bean.invokerRefreshValue());
        }
    }


    @AfterEach
    void afterEach() {
        assertEquals("A", AProtocolWrapper.message);
        assertEquals("B", BProtocolWrapper.message);
    }

}

