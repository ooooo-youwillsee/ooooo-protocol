package com.ooooo.protocol.core;

import com.ooooo.protocol.core.request.ProtocolProperties;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public interface Protocol {

    Object execute(Invocation invocation) throws Throwable;

    ProtocolProperties getProperties();

}