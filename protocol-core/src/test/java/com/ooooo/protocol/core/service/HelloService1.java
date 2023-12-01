package com.ooooo.protocol.core.service;

import com.ooooo.protocol.core.annotation.APIMapping;
import com.ooooo.protocol.core.annotation.APIService;
import lombok.Data;

@APIService
public interface HelloService1 {

    String INVOKE_NORMAL_METHOD = "HelloService1#invokeNormalMethod";

    String INVOKE_DEFAULT_VALUE = "HelloService1#invokeDefaultValue";

    String INVOKER_REFRESH_VALUE = "HelloService1#invokerRefreshValue";

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

