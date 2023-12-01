package com.ooooo.protocol.core.request;

import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.beans.ProtocolWrapper;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class BProtocolWrapper extends ProtocolWrapper {

    public static String message;

    public BProtocolWrapper(Protocol protocol) {
        super(protocol);
    }

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        message = "B";
        return protocol.execute(invocation);
    }
}
