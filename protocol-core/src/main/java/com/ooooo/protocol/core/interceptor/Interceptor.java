package com.ooooo.protocol.core.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.core.Ordered;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @see MethodInterceptor
 * @since 1.0.0
 */
public interface Interceptor extends Ordered {

    Object invoke(InterceptorInvocation invocation) throws Throwable;
}
