package com.ooooo.protocol.core.context;

import com.ooooo.protocol.core.annotation.APIMapping;
import com.ooooo.protocol.core.exception.APIException;
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

    private String className;

    private String methodName;

    private String note;

    private String url;

    public APIMethodConfig(Method method) {
        APIMapping apiMapping = method.getAnnotation(APIMapping.class);
        if (apiMapping == null) {
            throw new APIException("there is no @APIMapping in method " + method);
        }
        this.note = apiMapping.note();
        this.url= apiMapping.value();
        this.methodName = method.getName();
        this.className = method.getDeclaringClass().getName();
    }
}
