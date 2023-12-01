package com.ooooo.protocol.core.request;

import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.exception.APIServiceException;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class EmptyProtocol implements Protocol {

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        throw new APIServiceException("未实现的协议");
    }

    @Override
    public ProtocolProperties getProperties() {
        return null;
    }
}
