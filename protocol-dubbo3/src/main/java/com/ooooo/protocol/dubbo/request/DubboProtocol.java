package com.ooooo.protocol.dubbo.request;

import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.context.APIServiceContext;
import com.ooooo.protocol.core.request.ProtocolProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.RegistryConstants;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.RegistryConfig;

/**
 * dubbo 实现
 *
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
public class DubboProtocol implements Protocol {

    @Getter
    private final ProtocolProperties properties;

    private final DubboProtocolConfig config;

    private volatile DubboInvoker invoker;

    public DubboProtocol(ProtocolProperties properties) {
        this.properties = properties;
        this.config = properties.resolveProtocolConfig(DubboProtocolConfig.class);
    }

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        initialize();

        DubboInvocation dubboInvocation = new DubboInvocation(invocation);
        String group = dubboInvocation.getGroup();
        String version = dubboInvocation.getVersion();
        String interfaceName = dubboInvocation.getInterfaceName();
        String methodName = dubboInvocation.getMethodName();
        Object[] args = dubboInvocation.getArguments();
        return invoker.invoke(group, version, interfaceName, methodName, args);
    }

    /**
     * @see RegistryConstants#REGISTRY_CLUSTER_KEY
     */
    private void initialize() {
        if (invoker == null) {
            synchronized (this) {
                if (invoker == null) {
                    invoker = new DubboInvoker();
                    invoker.setRegistryConfig(config.getRegistry());
                    invoker.setApplicationConfig(config.getApplication());

                    if (config.getMetadataReport() == null) {
                        // use register config instead of metadata report config
                        RegistryConfig registry = config.getRegistry();
                        MetadataReportConfig metadataReportConfig = new MetadataReportConfig();
                        metadataReportConfig.setAddress(registry.getAddress());
                        metadataReportConfig.setGroup(registry.getGroup());
                        metadataReportConfig.setParameters(registry.getParameters());
                        config.setMetadataReport(metadataReportConfig);
                    }
                    invoker.setMetadataReportConfig(config.getMetadataReport());

                    // link
                    String protocolId = APIServiceContext.getAPIServiceConfig().getProtocolId();
                    String id = DubboProtocol.class.getSimpleName() + "#" + protocolId;
                    invoker.getRegistryConfig().setId(id);
                    invoker.getMetadataReportConfig().setId(id);

                    // init dubbo config
                    invoker.initializeDubbo();
                }
            }
        }
    }

}
