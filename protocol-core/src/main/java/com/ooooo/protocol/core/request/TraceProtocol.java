package com.ooooo.protocol.core.request;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.beans.ProtocolWrapper;
import com.ooooo.protocol.core.constants.ProtocolWrapperOrder;
import com.ooooo.protocol.core.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
@Order(ProtocolWrapperOrder.TRACE)
public class TraceProtocol extends ProtocolWrapper {

    @Autowired(required = false)
    private TraceService traceService;

    public TraceProtocol(Protocol protocol) {
        super(protocol);
    }

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        boolean enabled = isEnabled();
        if (enabled) {
            try {
                traceService.invokeBefore(invocation);
            } catch (Throwable e) {
                log.error("traceService invokeBefore failure, error: {}", e.getMessage());
            }
        }

        Throwable throwable = null;
        Object result = null;
        try {
            result = protocol.execute(invocation);
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            if (enabled) {
                try {
                    traceService.invokeAfter(invocation, result, throwable);
                } catch (Throwable e) {
                    log.error("traceService invokeAfter failure, error: {}", e.getMessage());
                }
            }
        }
        return result;
    }

    private boolean isEnabled() {
        return traceService != null && properties.isTrace();
    }

}
