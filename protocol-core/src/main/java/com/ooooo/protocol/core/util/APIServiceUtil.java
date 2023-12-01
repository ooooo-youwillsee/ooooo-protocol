package com.ooooo.protocol.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.exception.APIException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class APIServiceUtil {

    /**
     * @param returnType
     * @param data
     */
    public static Object parseData(Class<?> returnType, Object data) {
        if (data == null || returnType == null) {
            return null;
        }

        if (returnType.isInstance(data)) {
            return data;
        }

        // byte[]
        if (data instanceof byte[]) {
            data = new String((byte[]) data, StandardCharsets.UTF_8);
            return parseData(returnType, data);
        }

        // json
        if (data instanceof String) {
            String json = (String) data;
            json = json.trim();

            List<Map<String, Object>> mapList;
            if (json.startsWith("[")) {
                mapList = JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
                });
            } else {
                mapList = Collections.singletonList(JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
                }));
            }
            return parseData(returnType, mapList);
        }

        // mapList
        if (data instanceof List) {
            List<?> mapList = (List<?>) data;

            Object obj = null;
            if (Collection.class.isAssignableFrom(returnType)) {
                Class<?> clazz = ResolvableType.forClass(returnType).resolveGeneric(0);
                obj = JSON.parseArray(JSON.toJSONString(mapList), clazz);
            } else {
                obj = CollectionUtils.isEmpty(mapList) ? Collections.emptyMap() : mapList.get(0);
                obj = JSON.parseObject(JSON.toJSONString(obj), returnType);
            }
            return obj;
        }

        throw new APIException("The data isn't handled. data: " + data);
    }


    public static void checkConfigValue(String value, String configKey) {
        if (StrUtil.isBlank(value)) {
            throw new APIException("the property['" + configKey + "'] isn't exist in config");
        }
    }


    public static Protocol getProtocol(ApplicationContext applicationContext, String protocolId) {
        if (protocolId == null) {
            throw new IllegalArgumentException("protocolId is null");
        }

        String[] beanNames = applicationContext.getBeanNamesForType(Protocol.class);
        for (String beanName : beanNames) {
            if (beanName.equals(protocolId)) {
                return applicationContext.getBean(beanName, Protocol.class);
            }
        }
        throw new APIException("protocolId['" + protocolId + "'] of" +
                " " + Protocol.class + " doesn't configure");
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> toStringStringMapUsingJson(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }
        if (obj instanceof Map) {
            return (Map<String, String>) obj;
        }

        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<Map<String, String>>() {});
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toStringObjectMapUsingJson(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }

        Map<String, Object> stringObjectMap = JSON.parseObject(JSON.toJSONString(obj), new TypeReference<Map<String, Object>>() {});
        // override value, support byte[]
        BeanUtil.beanToMap(obj, stringObjectMap, CopyOptions.create());
        return stringObjectMap;
    }

}
