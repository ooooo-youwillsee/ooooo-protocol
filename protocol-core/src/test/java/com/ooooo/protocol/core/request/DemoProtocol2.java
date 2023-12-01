package com.ooooo.protocol.core.request;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.Invocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DemoProtocol2 implements Protocol {

    @Getter
    private ProtocolProperties properties;

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        return "DemoProtocol2";
    }

}
