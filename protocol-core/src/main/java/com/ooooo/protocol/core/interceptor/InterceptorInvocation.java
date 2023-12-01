package com.ooooo.protocol.core.interceptor;

import com.ooooo.protocol.core.Invocation;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class InterceptorInvocation implements Invocation {

    private int currentIndex = 0;

    @Getter
    private Method method;

    @Getter
    private Object[] arguments;

    @Getter
    private List<Interceptor> interceptors;

    public InterceptorInvocation(Method method, Object[] arguments, List<Interceptor> interceptors) {
        this.method = method;
        this.arguments = arguments;
        this.interceptors = interceptors;
    }

    public Object proceed() throws Throwable {
        if (currentIndex < interceptors.size()) {
            return interceptors.get(currentIndex++).invoke(this);
        }
        throw new IllegalArgumentException("not execute proceed.");
    }

    public void resetInterceptor(int interceptor) {
        for (int i = 0; i < interceptors.size(); i++) {
            int order = interceptors.get(i).getOrder();
            if (order == interceptor) {
                currentIndex = i;
                return;
            }
        }

        throw new IllegalArgumentException("interceptor order " + interceptor + "not found.");
    }
}
