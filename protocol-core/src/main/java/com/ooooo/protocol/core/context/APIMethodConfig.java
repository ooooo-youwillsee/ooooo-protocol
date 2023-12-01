package com.ooooo.protocol.core.context;

import com.ooooo.protocol.core.annotation.APIMapping;
import com.ooooo.protocol.core.util.APIServiceUtil;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Setter
@Getter
public class APIMethodConfig {

    private Class<?> declaringClass;

    private Method method;

    private String note;

    private String url;

    public APIMethodConfig(Method method) {
        APIMapping apiMapping = APIServiceUtil.getAnnotation(method, APIMapping.class);
        this.note = apiMapping.note();
        this.url = apiMapping.value();
        this.declaringClass = method.getDeclaringClass();
        this.method = method;
    }
}
