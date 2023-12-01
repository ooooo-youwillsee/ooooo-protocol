package com.ooooo.protocol.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class RefreshProtocolBeanEvent extends ApplicationEvent {

    public RefreshProtocolBeanEvent(Object source) {
        super(source);
    }
}
