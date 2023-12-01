package com.ooooo.protocol.http.request;

import com.ooooo.protocol.core.request.ProtocolProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
public abstract class RegexRoutingHttpProtocol extends HttpProtocol {

    private final Map<String, RegexRoutingHttpProtocolConfig> configMap;

    public RegexRoutingHttpProtocol(ProtocolProperties protocolProperties) {
        super(protocolProperties);
        configMap = protocolProperties.resolveProtocolConfigs(RegexRoutingHttpProtocolConfig.class);
    }


    protected HttpProtocolConfig getConfig(HttpInvocation invocation) {
        for (Entry<String, RegexRoutingHttpProtocolConfig> entry : configMap.entrySet()) {
            RegexRoutingHttpProtocolConfig config = entry.getValue();
            if (config.match(invocation.getUrl())) {
                return config;
            }
        }
        return super.getConfig(invocation);
    }

}
