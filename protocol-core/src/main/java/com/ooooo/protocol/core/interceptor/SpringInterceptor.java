package com.ooooo.protocol.core.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class SpringInterceptor implements MethodInterceptor {

    private final List<Interceptor> interceptors;

    public SpringInterceptor(ApplicationContext applicationContext) {
        this.interceptors = new ArrayList<>(applicationContext.getBeansOfType(Interceptor.class).values());
        AnnotationAwareOrderComparator.sort(this.interceptors);
    }

    @Override
    public Object invoke(MethodInvocation springInvocation) throws Throwable {
        Method method = springInvocation.getMethod();
        Object[] arguments = springInvocation.getArguments();
        InterceptorInvocation invocation = new InterceptorInvocation(method, arguments, interceptors);
        return invocation.proceed();
    }


}
