package com.ooooo.protocol.core.annotation;

import com.ooooo.protocol.core.context.APIServiceConfig;
import org.springframework.aop.framework.ProxyFactoryBean;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public interface IAPIService {

    APIServiceConfig getAPIServiceConfig();

    ProxyFactoryBean getProxyFactoryBean();
}
