package com.ooooo.protocol.dubbo.request;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.common.ServiceKey;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.JsonUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.MetadataReportFactory;
import org.apache.dubbo.metadata.report.MetadataReportInstance;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.registry.client.metadata.MetadataServiceNameMapping;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static cn.hutool.core.util.ReflectUtil.getFieldValue;
import static org.apache.dubbo.common.constants.CommonConstants.GROUP_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class DubboInvoker {

    private final Map<ServiceKey, GenericService> serviceMap = new ConcurrentHashMap<>();

    @Setter
    private ApplicationConfig applicationConfig;

    @Setter
    @Getter
    private RegistryConfig registryConfig;

    @Setter
    @Getter
    private MetadataReportConfig metadataReportConfig;

    private MetadataReport metadataReport;

    /**
     * @see MetadataServiceNameMapping#getAndListen(URL, org.apache.dubbo.metadata.MappingListener)
     */
    public synchronized void initializeDubbo() {
        if (metadataReport == null) {
            metadataReport = getMetadataReport();
        }

        ApplicationModel applicationModel = ApplicationModel.defaultModel();

        // init metadataReport -> serviceNameMapping
        MetadataReportInstance metadataReportInstance =
                applicationModel.getBeanFactory().getBean(MetadataReportInstance.class);
        @SuppressWarnings("unchecked")
        Map<String, MetadataReport> metadataReportMap =
                (Map<String, MetadataReport>) getFieldValue(metadataReportInstance, "metadataReports");
        metadataReportMap.putIfAbsent(metadataReportConfig.getId(), metadataReport);
    }

    public Object invoke(String group, String version, String interfaceName, String methodName, Object[] args) {
        FullServiceDefinition serviceDefinition = getServiceDefinition(group, version, interfaceName);
        GenericService genericService = getOrCreateGenericService(group, version, interfaceName, serviceDefinition);
        String[] parameterTypes = getParameterTypes(methodName, args, serviceDefinition);
        Object result = genericService.$invoke(methodName, parameterTypes, args);
        return result;
    }

    private FullServiceDefinition getServiceDefinition(String group, String version, String interfaceName) {
        // service definition
        MetadataIdentifier metadataIdentifier = new MetadataIdentifier(interfaceName, version, group, PROVIDER_SIDE,
                applicationConfig.getName());
        String serviceDefinitionStr = metadataReport.getServiceDefinition(metadataIdentifier);
        if (StrUtil.isBlank(serviceDefinitionStr)) {
            throw new IllegalArgumentException("not found methodName['" + interfaceName + "'] metadata definition.");
        }
        FullServiceDefinition serviceDefinition = JsonUtils.getJson().toJavaObject(serviceDefinitionStr,
                FullServiceDefinition.class);
        return serviceDefinition;
    }


    private String[] getParameterTypes(String methodName, Object[] args, ServiceDefinition serviceDefinition) {
        // handle method definition
        for (MethodDefinition method : serviceDefinition.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == args.length) {
                return method.getParameterTypes();
            }
        }
        throw new IllegalArgumentException("not found methodName ['" + methodName + "'] metadata definition.");
    }

    private GenericService getOrCreateGenericService(String group, String version, String interfaceName,
                                                     FullServiceDefinition serviceDefinition) {
        ServiceKey serviceKey = new ServiceKey(interfaceName, version, group);
        GenericService genericService = serviceMap.computeIfAbsent(serviceKey, __ -> createGenericService(serviceKey,
                serviceDefinition));
        return genericService;
    }

    private GenericService createGenericService(ServiceKey serviceKey, FullServiceDefinition serviceDefinition) {
        // generic service
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setGeneric("true");
        referenceConfig.setInterface(serviceKey.getInterfaceName());
        referenceConfig.setGroup(serviceKey.getGroup());
        referenceConfig.setVersion(serviceKey.getVersion());
        referenceConfig.setTag(serviceDefinition.getParameters().get(CommonConstants.TAG_KEY));
        referenceConfig.setParameters(applicationConfig.getParameters());

        // register generic service
        DubboBootstrap instance = DubboBootstrap.getInstance();
        instance.reference(referenceConfig);

        return referenceConfig.get();
    }

    private MetadataReport getMetadataReport() {
        // metadata://
        URL url = getMetadataReportUrl();
        // metadataReport
        ExtensionLoader<MetadataReportFactory> loader =
                ApplicationModel.defaultModel().getExtensionLoader(MetadataReportFactory.class);
        MetadataReportFactory metadataReportFactory = loader.getAdaptiveExtension();
        MetadataReport metadataReport = metadataReportFactory.getMetadataReport(url);
        return metadataReport;
    }

    private URL getMetadataReportUrl() {
        URL url = URL.valueOf(metadataReportConfig.getAddress());
        for (Entry<String, String> entry : metadataReportConfig.getParameters().entrySet()) {
            url = url.addParameter(entry.getKey(), entry.getValue());
        }
        url = url.addParameter(GROUP_KEY, metadataReportConfig.getGroup());
        return url;
    }
}
