package com.ooooo.protocol.core.beans;

import com.ooooo.protocol.core.request.MockProtocol;
import com.ooooo.protocol.core.request.RetryProtocol;
import com.ooooo.protocol.core.request.TraceProtocol;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @see ProtocolWrapper
 * @see ProtocolWrapperCustomizer
 * @since 1.0.0
 */
public class ProtocolWrapperHelper implements ApplicationContextAware, InitializingBean {

    @Setter
    private ApplicationContext applicationContext;

    @Getter
    public final List<Class<? extends ProtocolWrapper>> protocolWrapperClasses = new ArrayList<>(Arrays.asList(
            TraceProtocol.class, MockProtocol.class, RetryProtocol.class
    ));

    @Override
    public void afterPropertiesSet() throws Exception {
        customizeProtocolWrappers();
        sortProtocolWrappers();
    }

    private void customizeProtocolWrappers() {
        Map<String, ProtocolWrapperCustomizer> beanMap =
                applicationContext.getBeansOfType(ProtocolWrapperCustomizer.class);
        for (Map.Entry<String, ProtocolWrapperCustomizer> entry : beanMap.entrySet()) {
            ProtocolWrapperCustomizer customizer = entry.getValue();
            customizer.customize(protocolWrapperClasses);
        }
    }

    private void sortProtocolWrappers() {
        protocolWrapperClasses.sort(AnnotationAwareOrderComparator.INSTANCE);
    }


}
