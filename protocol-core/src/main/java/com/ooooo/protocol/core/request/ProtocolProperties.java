package com.ooooo.protocol.core.request;

import com.alibaba.fastjson.JSON;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.beans.ProtocolWrapper;
import com.ooooo.protocol.core.service.MockService;
import com.ooooo.protocol.core.service.TraceService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


@Data
@NoArgsConstructor
public class ProtocolProperties {

    private List<String> basePackages;

    /**
     * @see Protocol
     */
    private Class<? extends Protocol> protocolClass;

    private List<Class<? extends ProtocolWrapper>> protocolWrapperClass;

    private String desc;

    /**
     * @see TraceProtocol
     * @see TraceService
     */
    private boolean trace = false;

    /**
     * @see MockProtocol
     * @see MockService
     */
    private boolean mock = false;

    /**
     * 单一配置，会映射到 protocolConfigClass
     *
     * @see Protocol
     */
    private Map<String, Object> config = new LinkedHashMap<>();

    /**
     * 多个配置, 会映射到 protocolConfigClass,
     * <p>
     * Map&lt;String, Object&gt; 就是一个 config 对象
     */
    private Map<String, Map<String, Object>> configs = new LinkedHashMap<>();

    /**
     * @param clazz
     * @param <T>
     * @return
     * @see ProtocolProperties#getConfig()
     */
    public <T> T resolveProtocolConfig(Class<T> clazz) {
        return convert(config, clazz);
    }

    /**
     * @param clazz
     * @param <T>
     * @return
     * @see ProtocolProperties#getConfigs()
     */
    public <T> Map<String, T> resolveProtocolConfigs(Class<T> clazz) {
        Map<String, T> configMap = new LinkedHashMap<>();
        for (Entry<String, Map<String, Object>> entry : configs.entrySet()) {
            String configId = entry.getKey();
            // override config
            HashMap<String, Object> configValue = new HashMap<>(config);
            configValue.putAll(entry.getValue());

            T config = convert(configValue, clazz);
            configMap.put(configId, config);
        }
        return configMap;
    }


    private static <T> T convert(Map<String, Object> config, Class<T> clazz) {
        config = config == null ? new HashMap<>() : config;
        T t = JSON.parseObject(JSON.toJSONString(config), clazz);
        return t;
    }

}