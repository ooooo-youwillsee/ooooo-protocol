package com.ooooo.protocol.core.bootstrap;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.ooooo.protocol.core.annotation.EnableAPIServiceLazyLoad;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class APIServiceLazyLoadImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
                                        BeanNameGenerator importBeanNameGenerator) {
        MultiValueMap<String, Object> allAnnotationAttributes =
                importingClassMetadata.getAllAnnotationAttributes(EnableAPIServiceLazyLoad.class.getName());

        if (allAnnotationAttributes != null) {
            List<Object> basePackagesList = allAnnotationAttributes.get("basePackages");
            for (Object basePackages : basePackagesList) {
                registerClassPathAPIServiceLazyLoadScanner(importingClassMetadata, (String[]) basePackages, registry);
            }
        }
    }

    private void registerClassPathAPIServiceLazyLoadScanner(AnnotationMetadata importingClassMetadata,
                                                            String[] basePackages, BeanDefinitionRegistry registry) {
        if (ArrayUtil.isEmpty(basePackages)) {
            basePackages = new String[]{ClassUtils.getPackageName(importingClassMetadata.getClassName())};
        }
        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder.genericBeanDefinition(APIServiceLazyLoadScannerConfigurer.class);
        builder.addPropertyValue("basePackages", StrUtil.join(StrUtil.COMMA, (Object[]) basePackages));

        String beanName =
                importingClassMetadata.getClassName() + "#" + APIServiceLazyLoadScannerConfigurer.class.getName();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), generateBeanName(beanName
                , registry));
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }


    private static String generateBeanName(String beanName, BeanDefinitionRegistry registry) {
        int i = 0;
        String newBeanName;
        while (true) {
            newBeanName = beanName + i;
            if (!registry.containsBeanDefinition(newBeanName)) {
                break;
            }
            i++;
        }
        return newBeanName;
    }
}
