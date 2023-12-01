package com.ooooo.protocol.dubbo.annotation;

import com.ooooo.protocol.core.annotation.APIMapping;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@APIMapping
@Inherited
public @interface DubboMapping {

    @AliasFor(annotation = APIMapping.class, attribute = "value")
    String value() default "";

    @AliasFor(annotation = APIMapping.class, attribute = "note")
    String note() default "";

    /**
     * dubbo 接口的 group 属性
     * @return
     */
    String group() default "";

    /**
     * dubbo 接口的 version 属性
     * @return
     */
    String version() default "";
}
