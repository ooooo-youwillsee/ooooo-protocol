package com.ooooo.protocol.dubbo.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.RegistryConfig;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Getter
@Setter
public class DubboProtocolConfig {

    private ApplicationConfig application;

    private RegistryConfig registry;

    private MetadataReportConfig metadataReport;

}
