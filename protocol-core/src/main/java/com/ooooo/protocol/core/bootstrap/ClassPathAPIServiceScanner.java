package com.ooooo.protocol.core.bootstrap;

import com.ooooo.protocol.core.annotation.APIService;
import com.ooooo.protocol.core.annotation.IAPIService;
import com.ooooo.protocol.core.context.APIServiceConfig;
import com.ooooo.protocol.core.interceptor.SpringInterceptor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
public class ClassPathAPIServiceScanner extends ClassPathBeanDefinitionScanner {

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private String protocolId;

    public ClassPathAPIServiceScanner(BeanDefinitionRegistry registry) {
        super(registry);
        this.setBeanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator());
    }

    @Override
    protected void registerDefaultFilters() {
        addIncludeFilter(new AnnotationTypeFilter(APIService.class));
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            log.warn("No APIService was found in '{}' package. Please check your configuration.", Arrays.toString(basePackages));
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        for (BeanDefinitionHolder holder : beanDefinitions) {
            AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) holder.getBeanDefinition();
            APIServiceConfig config = new APIServiceConfig();
            config.setProtocolId(protocolId);
            config.setApplicationContext(applicationContext);
            finishBeanDefinition(beanDefinition, config);
        }
    }

    protected void finishBeanDefinition(AbstractBeanDefinition beanDefinition, APIServiceConfig config) {
        String beanClassName = beanDefinition.getBeanClassName();
        Class<?> beanClass = ClassUtils.resolveClassName(beanClassName, Thread.currentThread().getContextClassLoader());
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setBeanClassName(beanClassName);

        beanDefinition.setInstanceSupplier(() -> {
            ProxyFactoryBean proxyBean = new ProxyFactoryBean();
            proxyBean.addInterface(IAPIService.class);
            proxyBean.addInterface(beanClass);
            proxyBean.setExposeProxy(true);
            proxyBean.setTarget(config);

            // extension
            proxyBean.addAdvice(0, (MethodInterceptor) invocation -> {
                String name = invocation.getMethod().getName();
                if (name.equals("toString")) {
                    return beanClassName;
                }
                if (name.equals("getAPIServiceConfig")) {
                    return config;
                }
                if (name.equals("getProxyFactoryBean")) {
                    return proxyBean;
                }
                return invocation.proceed();
            });

            proxyBean.addAdvice(new SpringInterceptor(applicationContext));
            return proxyBean;
        });
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return beanDefinition instanceof AbstractBeanDefinition && !metadata.isAnnotation();
    }
}
