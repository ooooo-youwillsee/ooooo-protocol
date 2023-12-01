package com.ooooo.protocol.core.bootstrap;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class APIServiceLazyLoadScannerConfigurer implements BeanDefinitionRegistryPostProcessor, PriorityOrdered,
        ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private String[] basePackages;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathAPIServiceLazyLoadScanner scanner = new ClassPathAPIServiceLazyLoadScanner(registry);
        scanner.scan(basePackages);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // no implementation
    }


    @Override
    public int getOrder() {
        return APIServiceProtocolPropertiesConfigurer.ORDER + 10;
    }

}
