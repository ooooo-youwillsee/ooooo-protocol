package com.ooooo.protocol.core.context;

import lombok.Getter;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Getter
public class APIServiceConfig {

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private String protocolId;

    private final List<Advice> tmpAdvices = new CopyOnWriteArrayList<>();

}
