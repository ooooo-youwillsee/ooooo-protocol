package com.ooooo.protocol.core.bootstrap;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.constants.ProtocolConstants;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */

public class APIServiceProtocolPropertiesRefresher implements BeanFactoryAware, EnvironmentAware, ApplicationListener<EnvironmentChangeEvent> {

    @Setter
    private Environment environment;

    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private APIServiceProtocolPropertiesConfigurer configurer;

    public void refreshProtocolBean() {
        APIServiceProperties oldApiServiceProperties = configurer.getProperties();
        APIServiceProperties apiServiceProperties = Binder.get(environment)
                .bindOrCreate(ProtocolConstants.PREFIX_OOOOO, APIServiceProperties.class);
        if (oldApiServiceProperties.equals(apiServiceProperties)) {
            return;
        }

        // copy apiServiceProperties
        BeanUtils.copyProperties(apiServiceProperties, oldApiServiceProperties);

        // deregister protocol bean
        String[] protocolBeanNames = beanFactory.getBeanNamesForType(Protocol.class);
        for (String protocolBeanName : protocolBeanNames) {
            beanFactory.removeBeanDefinition(protocolBeanName);
            beanFactory.destroySingleton(protocolBeanName);
        }

        // register new protocol bean
        configurer.setProperties(apiServiceProperties);
        configurer.registerProtocolBeans(beanFactory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        refreshProtocolBean();
    }

}
