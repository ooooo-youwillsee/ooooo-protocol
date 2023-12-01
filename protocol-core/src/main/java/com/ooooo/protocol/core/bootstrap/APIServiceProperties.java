package com.ooooo.protocol.core.bootstrap;

import com.ooooo.protocol.core.constants.ProtocolConstants;
import com.ooooo.protocol.core.request.ProtocolProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = ProtocolConstants.PREFIX_OOOOO)
public class APIServiceProperties {

    private Map<String /* protocolId  */, ProtocolProperties> protocol = new LinkedHashMap<>();

}
