package com.ooooo.protocol.core.bootstrap;

import cn.hutool.core.collection.CollUtil;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.beans.ProtocolWrapper;
import com.ooooo.protocol.core.beans.ProtocolWrapperHelper;
import com.ooooo.protocol.core.event.RefreshProtocolBeanEvent;
import com.ooooo.protocol.core.request.ProtocolProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
public class APIServiceProtocolPropertiesConfigurer implements BeanDefinitionRegistryPostProcessor,
        ApplicationContextAware, PriorityOrdered {

    public static final int ORDER = 10;

    @Setter
    @Getter
    private APIServiceProperties properties;

    @Setter
    private ApplicationContext applicationContext;

    private final ProtocolWrapperHelper protocolWrapperHelper;

    public APIServiceProtocolPropertiesConfigurer(APIServiceProperties properties,
                                                  ProtocolWrapperHelper protocolWrapperHelper) {
        this.properties = properties;
        this.protocolWrapperHelper = protocolWrapperHelper;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        scanAPIServiceBeans(registry);
        registerProtocolBeans(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private void scanAPIServiceBeans(BeanDefinitionRegistry registry) {
        ClassPathAPIServiceScanner scanner = new ClassPathAPIServiceScanner(registry);
        Map<String, ProtocolProperties> protocol = properties.getProtocol();
        for (Entry<String, ProtocolProperties> entry : protocol.entrySet()) {
            String protocolId = entry.getKey();
            ProtocolProperties protocolProperties = entry.getValue();
            List<String> basePackages = protocolProperties.getBasePackages();
            if (CollUtil.isEmpty(basePackages)) {
                throw new IllegalArgumentException("protocolId: '" + protocolId + "', basePackages is null");
            }
            scanner.setProtocolId(protocolId);
            scanner.setApplicationContext(applicationContext);
            scanner.doScan(basePackages.toArray(new String[0]));
        }
    }

    protected void registerProtocolBeans(BeanDefinitionRegistry registry) {
        Map<String, ProtocolProperties> protocol = properties.getProtocol();
        for (Entry<String, ProtocolProperties> entry : protocol.entrySet()) {
            String protocolId = entry.getKey();
            ProtocolProperties protocolProperties = entry.getValue();
            AnnotatedGenericBeanDefinition beanDefinition = getBeanDefinition(registry, protocolId, protocolProperties);
            registry.registerBeanDefinition(protocolId, beanDefinition);

            log.info("register protocol bean success! properties: {}", protocolProperties);
        }
        // publish event to refresh protocol bean
        applicationContext.publishEvent(new RefreshProtocolBeanEvent(properties));
    }

    protected AnnotatedGenericBeanDefinition getBeanDefinition(BeanDefinitionRegistry registry, String protocolId,
                                                               ProtocolProperties protocolProperties) {
        List<Class<? extends ProtocolWrapper>> wrapperClasses =
                protocolWrapperHelper.getProtocolWrapperClasses();
        // real protocol bean Definition
        Class<?> protocolClass = protocolProperties.resolveProtocolClass();
        AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(protocolClass);
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(protocolProperties);

        // wrapper protocol
        String beanName = protocolClass.getSimpleName() + "#" + protocolId;
        for (int i = wrapperClasses.size() - 1; i >= 0; i--) {
            registry.registerBeanDefinition(beanName, beanDefinition);
            Class<? extends Protocol> clazz = wrapperClasses.get(i);
            beanDefinition = new AnnotatedGenericBeanDefinition(clazz);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(beanName));
            beanName = clazz.getSimpleName() + "#" + beanName;
        }

        return beanDefinition;
    }


    @Override
    public int getOrder() {
        return ORDER;
    }
}
