package com.ooooo.protocol.core;

import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public interface Invocation {

    Method getMethod();

    Object[] getArguments();

}
