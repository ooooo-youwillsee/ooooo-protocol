package com.ooooo.protocol.core.annotation;

import com.ooooo.protocol.core.bootstrap.APIServiceLazyLoadImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

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
@Target({ElementType.TYPE})
@Documented
@Inherited
@Import(APIServiceLazyLoadImportBeanDefinitionRegistrar.class)
public @interface EnableAPIServiceLazyLoad {

    /**
     * 默认值是当前包
     *
     * @return
     */
    String[] basePackages() default {};

}
