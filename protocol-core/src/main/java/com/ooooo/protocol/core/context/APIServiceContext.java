package com.ooooo.protocol.core.context;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.constants.InterceptorType;
import com.ooooo.protocol.core.interceptor.InterceptorInvocation;
import com.ooooo.protocol.core.request.ProtocolProperties;
import com.ooooo.protocol.core.util.APIServiceUtil;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class APIServiceContext {

    @Setter
    Map<String, Object> attachments;

    @Setter
    Invocation invocation;

    @Setter
    APIServiceConfig apiServiceConfig;

    @Setter
    APIMethodConfig apiMethodConfig;

    public static APIServiceContext current() {
        return APIServiceContextInterceptor.API_SERVICE_CONTEXT_THREAD_LOCAL.get();
    }

    public static ProtocolProperties getProtocolProperties() {
        APIServiceConfig apiServiceConfig = getAPIServiceConfig();
        if (apiServiceConfig == null || apiServiceConfig.getProtocolId() == null) {
            return null;
        }
        ApplicationContext applicationContext = apiServiceConfig.getApplicationContext();
        Protocol protocol = APIServiceUtil.getProtocol(applicationContext, apiServiceConfig.getProtocolId());
        return protocol.getProperties();
    }

    public static APIServiceConfig getAPIServiceConfig() {
        return current().apiServiceConfig;
    }

    public static APIMethodConfig getAPIMethodConfig() {
        return current().apiMethodConfig;
    }

    public static Invocation getInvocation() {
        return current().invocation;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAttachment(String key) {
        return (T) current().attachments.get(key);
    }

    public static void setAttachment(String key, Object value) {
        current().attachments.put(key, value);
    }

    public static Map<String, Object> getAttachments() {
        return current().attachments;
    }

    /**
     * @param interceptorOrder
     * @throws Throwable
     * @see InterceptorType
     */
    public static void jumpInterceptor(int interceptorOrder) throws Throwable {
        Invocation invocation = getInvocation();
        if (invocation instanceof InterceptorInvocation) {
            InterceptorInvocation i = (InterceptorInvocation) invocation;
            i.resetInterceptor(interceptorOrder);
            i.proceed();
        }
    }
}
