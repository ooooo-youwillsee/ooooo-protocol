## ooooo-protocol 

提供**基于接口的声明式方式**来对接第三方接口，支持**动态刷新**。


### 使用示例

配置如下:

```yaml
ooooo:
  protocol:
    # 配置的协议 id
    demo:
      # 扫描的包
      base-packages:
        - com.ooooo.protocol.core.service.**
      # 协议类 (需要自己编写)
      protocol-class: com.ooooo.protocol.core.request.DemoProtocol
      # 自定义包装类（需要自己编写），可以用于填充默认参数，校验参数，解析结果
      protocol-wrapper-class:
        - com.ooooo.protocol.core.request.AProtocolWrapper
        - com.ooooo.protocol.core.request.BProtocolWrapper
      # 协议的配置
      config:
        a: b
```

实现上述的 `DemoProtocol` 类:

(这里只是简单实现, 复杂可以看 `DubboProtocol`, `HttpProtocol`)

```java
@Getter
public class DemoProtocol implements Protocol {

    private ProtocolProperties properties;

    public DemoProtocol(ProtocolProperties properties) {
        this.properties = properties;
    }

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        String url = APIServiceContext.getAPIMethodConfig().getUrl();
        Object[] args = invocation.getArguments();
        String data = null;

        switch (url) {
            case HelloService.INVOKE_NORMAL_METHOD:
                data = (String) args[0];
                break;
            case HelloService.INVOKE_DEFAULT_VALUE:
                data = ((HelloService.DefaultValueHolder) args[0]).getName();
                break;
            case HelloService.INVOKER_REFRESH_VALUE:
                data = (String) properties.getConfig().get("a");
                break;
        }
       return data;
    }
}
```

声明自己的 **HelloService** 接口:

(配置 `base-packages` 会扫描到这个类，会被自动注册为 `spring` 的 `bean`，因此可以直接使用 `@Autowired`)

```java
@APIService
public interface HelloService {

    String INVOKE_NORMAL_METHOD = "HelloService#invokeNormalMethod";

    String INVOKE_DEFAULT_VALUE = "HelloService#invokeDefaultValue";

    String INVOKER_REFRESH_VALUE = "HelloService#invokerRefreshValue";

    @APIMapping(INVOKE_NORMAL_METHOD)
    String invokeNormalMethod(String name);

    @APIMapping(INVOKE_DEFAULT_VALUE)
    String invokeDefaultValue(DefaultValueHolder holder);

    @APIMapping(INVOKER_REFRESH_VALUE)
    String invokerRefreshValue();

    default String invokeDefaultMethod(String name) {
        return name + "123";
    }

    default String invokeDefaultMethodForNormalMethod(String name) {
        return invokeNormalMethod(name);
    }


    @Data
    class DefaultValueHolder {

        private String name;
    }

}
```

在代码中使用:

(这里只是一部分测试类，详细见 `DemoProtocolTest`)

```
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
}
```

### 模块说明

1. [protocol-core](protocol-core) 实现了对接第三方接口的核心逻辑, 
2. [protocol-dubbo](protocol-dubbo) 实现了对 dubbo 接口的适配
3. [protocol-http](protocol-http) 实现了对 http 接口的适配