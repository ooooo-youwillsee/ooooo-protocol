package com.ooooo.protocol.core.request;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.beans.ProtocolWrapper;
import com.ooooo.protocol.core.constants.ProtocolWrapperOrder;
import com.ooooo.protocol.core.service.MockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

/**
 * @author leizj
 * @since 1.0.0
 */
@Order(ProtocolWrapperOrder.MOCK)
public class MockProtocol extends ProtocolWrapper {

    @Autowired(required = false)
    protected MockService mockService;

    public MockProtocol(Protocol protocol) {
        super(protocol);
    }

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        if (mockService != null && properties.isMock() && mockService.hasMockData(invocation)) {
            return mockService.mock(invocation);
        }

        return protocol.execute(invocation);
    }

}
