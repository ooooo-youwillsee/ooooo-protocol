package com.ooooo.protocol.core;

import lombok.Getter;

import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class ProtocolInvocation implements Invocation {

    @Getter
    private Method method;

    @Getter
    private Object[] arguments;

    public ProtocolInvocation(Method method, Object[] arguments) {
        this.method = method;
        this.arguments = arguments;
    }
}
