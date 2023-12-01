package com.ooooo.protocol.core.context;

import com.ooooo.protocol.core.annotation.IAPIService;
import com.ooooo.protocol.core.constants.InterceptorType;
import com.ooooo.protocol.core.interceptor.Interceptor;
import com.ooooo.protocol.core.interceptor.InterceptorInvocation;
import org.springframework.aop.framework.AopContext;

import java.util.LinkedHashMap;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @see APIServiceConfig
 * @since 1.0.0
 */
public class APIServiceContextInterceptor implements Interceptor {

    static final ThreadLocal<APIServiceContext> API_SERVICE_CONTEXT_THREAD_LOCAL =
            ThreadLocal.withInitial(APIServiceContext::new);

    @Override
    public Object invoke(InterceptorInvocation invocation) throws Throwable {
        APIServiceContext oldContext = API_SERVICE_CONTEXT_THREAD_LOCAL.get();

        IAPIService service = (IAPIService) AopContext.currentProxy();
        APIServiceConfig apiServiceConfig = service.getAPIServiceConfig();

        // build new apiContext
        APIServiceContext newContext = new APIServiceContext();
        newContext.setApiServiceConfig(apiServiceConfig);
        newContext.setApiMethodConfig(new APIMethodConfig(invocation.getMethod()));
        newContext.setInvocation(invocation);
        newContext.setAttachments(new LinkedHashMap<>());

        API_SERVICE_CONTEXT_THREAD_LOCAL.set(newContext);
        try {
            return invocation.proceed();
        } finally {
            API_SERVICE_CONTEXT_THREAD_LOCAL.set(oldContext);
        }
    }

    @Override
    public int getOrder() {
        return InterceptorType.API_SERVICE_CONTEXT.getOrder();
    }
}
