package com.ooooo.protocol.http.annotation;

import com.ooooo.protocol.core.annotation.APIMapping;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

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
public @interface HttpMapping {

    @AliasFor(annotation = APIMapping.class, attribute = "value")
    String value() default "";

    @AliasFor(annotation = APIMapping.class, attribute = "note")
    String note() default "";

    /**
     * 请求方法
     */
    HttpMethod method() default HttpMethod.POST;

    /**
     * 头信息，content-type
     *
     * @return
     * @see MediaType#APPLICATION_JSON_VALUE
     * @see MediaType#APPLICATION_FORM_URLENCODED_VALUE
     * @see MediaType#MULTIPART_FORM_DATA_VALUE
     * @see MediaType#APPLICATION_OCTET_STREAM_VALUE
     */
    String contentType() default "";
}
