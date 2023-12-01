package com.ooooo.protocol.core.autoconfigure;

import com.ooooo.protocol.core.beans.ProtocolWrapperHelper;
import com.ooooo.protocol.core.bootstrap.APIServiceProperties;
import com.ooooo.protocol.core.bootstrap.APIServiceProtocolPropertiesConfigurer;
import com.ooooo.protocol.core.bootstrap.APIServiceProtocolPropertiesRefresher;
import com.ooooo.protocol.core.constants.ProtocolConstants;
import com.ooooo.protocol.core.context.APIServiceContextInterceptor;
import com.ooooo.protocol.core.interceptor.InterfaceDefaultMethodInterceptor;
import com.ooooo.protocol.core.interceptor.LogInterceptor;
import com.ooooo.protocol.core.interceptor.ProtocolExecuteInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class APIServiceAutoConfiguration {

    @Bean
    public APIServiceProperties apiServiceProperties(Environment environment) {
        return Binder.get(environment).bindOrCreate(ProtocolConstants.PREFIX_OOOOO, APIServiceProperties.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtocolWrapperHelper protocolWrapperHelper() {
        return new ProtocolWrapperHelper();
    }

    @Bean
    public APIServiceProtocolPropertiesConfigurer apiServiceProtocolScanPropertiesConfigurer(APIServiceProperties apiServiceProperties, ProtocolWrapperHelper protocolWrapperHelper) {
        return new APIServiceProtocolPropertiesConfigurer(apiServiceProperties, protocolWrapperHelper);
    }

    @Bean
    public APIServiceProtocolPropertiesRefresher apiServiceProtocolPropertiesRefresher() {
        return new APIServiceProtocolPropertiesRefresher();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogInterceptor logInterceptor() {
        return new LogInterceptor();
    }

    @Bean
    public InterfaceDefaultMethodInterceptor interfaceDefaultMethodInterceptor() {
        return new InterfaceDefaultMethodInterceptor();
    }

    @Bean
    public APIServiceContextInterceptor apiServiceContextInterceptor() {
        return new APIServiceContextInterceptor();
    }

    @Bean
    public ProtocolExecuteInterceptor protocolExecuteInterceptor() {
        return new ProtocolExecuteInterceptor();
    }


}
