package com.ooooo.protocol.core.interceptor;

import cn.hutool.core.map.MapUtil;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.ProtocolInvocation;
import com.ooooo.protocol.core.constants.InterceptorType;
import com.ooooo.protocol.core.context.APIServiceContext;
import com.ooooo.protocol.core.exception.APIException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
public final class ProtocolExecuteInterceptor implements Interceptor, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public Object invoke(InterceptorInvocation invocation) throws Throwable {
        String protocolId = APIServiceContext.getAPIServiceConfig().getProtocolId();
        Protocol protocol = MapUtil.get(getProtocolMap(), protocolId, Protocol.class);
        if (protocol == null) {
            throw new APIException("not found protocol for id " + protocolId);
        }
        return protocol.execute(new ProtocolInvocation(invocation.getMethod(), invocation.getArguments()));
    }

    @Override
    public int getOrder() {
        return InterceptorType.PROTOCOL.getOrder();
    }

    public Map<String, Protocol> getProtocolMap() {
        Map<String, Protocol> beans = null;
        try {
            beans = applicationContext.getBeansOfType(Protocol.class);
        } catch (BeansException e) {
            return null;
        }
        return beans;
    }
}
