package com.ooooo.protocol.core.beans;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.request.ProtocolProperties;
import lombok.Getter;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Getter
public abstract class ProtocolWrapper implements Protocol {

    protected final ProtocolProperties properties;

    protected final Protocol protocol;

    public ProtocolWrapper(Protocol protocol) {
        this.properties = protocol.getProperties();
        this.protocol = protocol;
    }


}
