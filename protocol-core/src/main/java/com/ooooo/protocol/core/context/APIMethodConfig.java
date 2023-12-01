package com.ooooo.protocol.core.context;

import com.ooooo.protocol.core.annotation.APIMapping;
import com.ooooo.protocol.core.exception.APIException;
import com.ooooo.protocol.core.util.APIServiceUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

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
        APIMapping apiMapping = APIServiceUtil.checkMethodAnnotation(method, APIMapping.class);
        this.note = apiMapping.note();
        this.url= apiMapping.value();
        this.methodName = method.getName();
        this.className = method.getDeclaringClass().getName();
    }
}
