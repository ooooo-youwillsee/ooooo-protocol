package com.ooooo.protocol.core.bootstrap;

import com.ooooo.protocol.core.annotation.APIService;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Set;

public class ClassPathAPIServiceLazyLoadScanner extends ClassPathBeanDefinitionScanner {

    private static final Object API_SERVICE_FAKE_OBJECT = new Object();

    private static final String ERROR_MESSAGE = "你正在调用 APIService 接口，请配置 ooooo.protocol ";

    public ClassPathAPIServiceLazyLoadScanner(BeanDefinitionRegistry registry) {
        super(registry);
        setBeanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator());
    }

    @Override
    protected void registerDefaultFilters() {
        addIncludeFilter(new AnnotationTypeFilter(APIService.class));
    }

    /**
     * @param basePackages the packages to check for annotated classes
     * @return
     * @see #checkCandidate(String, BeanDefinition)
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        processBeanDefinitions(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    protected void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        for (BeanDefinitionHolder holder : beanDefinitionHolders) {
            AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) holder.getBeanDefinition();

            String beanClassName = beanDefinition.getBeanClassName();
            Class<?> beanClass = ClassUtils.resolveClassName(beanClassName, Thread.currentThread().getContextClassLoader());

            makeFakeBeanDefinition(beanDefinition, beanClass);
        }
    }

    /**
     * 做一个虚假的beanDefinition, 让spring的依赖检查成功
     */
    protected void makeFakeBeanDefinition(AbstractBeanDefinition beanDefinition, Class<?> beanClass) {
        beanDefinition.setInstanceSupplier(() -> {
            ProxyFactory factory = new ProxyFactory(API_SERVICE_FAKE_OBJECT);

            factory.addInterface(beanClass);
            factory.addAdvice((MethodInterceptor) invocation -> {
                throw new IllegalArgumentException(ERROR_MESSAGE);
            });

            Object proxy = factory.getProxy();
            return proxy;
        });
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return beanDefinition instanceof AbstractBeanDefinition && !metadata.isAnnotation();
    }
}