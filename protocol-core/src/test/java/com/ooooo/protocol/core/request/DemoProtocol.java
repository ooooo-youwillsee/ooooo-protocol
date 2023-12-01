package com.ooooo.protocol.core.request;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.context.APIServiceContext;
import com.ooooo.protocol.core.service.HelloService1;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DemoProtocol implements Protocol {

    private ProtocolProperties properties;

    public DemoProtocol(ProtocolProperties properties) {
        this.properties = properties;
    }

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        String url = APIServiceContext.getAPIMethodConfig().getUrl();
        Object[] args = invocation.getArguments();
        String data = null;

        switch (url) {
            case HelloService1.INVOKE_NORMAL_METHOD:
                data = (String) args[0];
                break;
            case HelloService1.INVOKE_DEFAULT_VALUE:
                data = ((HelloService1.DefaultValueHolder) args[0]).getName();
                break;
            case HelloService1.INVOKER_REFRESH_VALUE:
                data = (String) properties.getConfig().get("a");
                break;

        }
       return data;
    }

}
