package com.ooooo.protocol.core.interceptor;

import com.ooooo.protocol.core.constants.InterceptorType;
import org.springframework.aop.framework.AopContext;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * <p>note: it's jdk8 way which invoke interface default method by reflect.</p>
 *
 * <a href="https://stackoverflow.com/questions/22614746/how-do-i-invoke-java-8-default-methods-reflectively">stackoverflow</a>
 *
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class InterfaceDefaultMethodInterceptor implements Interceptor {

    @Override
    public Object invoke(InterceptorInvocation invocation) throws Throwable {
        Object proxy = AopContext.currentProxy();
        Method method = invocation.getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        Object[] arguments = invocation.getArguments();

        if (isDefaultMethod(declaringClass, method)) {
            Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            return constructor.newInstance(declaringClass)
                    .in(declaringClass)
                    .unreflectSpecial(method, declaringClass)
                    .bindTo(proxy)
                    .invokeWithArguments(arguments);
        }

        return invocation.proceed();
    }

    private boolean isDefaultMethod(Class<?> declaringClass, Method method) {
        return declaringClass.isInterface() && method.isDefault();
    }

    @Override
    public int getOrder() {
        return InterceptorType.INTERFACE_DEFAULT_METHOD.getOrder();
    }
}
