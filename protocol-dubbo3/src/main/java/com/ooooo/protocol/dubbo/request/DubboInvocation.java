package com.ooooo.protocol.dubbo.request;

import cn.hutool.core.util.StrUtil;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.util.APIServiceUtil;

import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class DubboInvocation implements Invocation {

    private Invocation invocation;

    public DubboInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public String getGroup() {
        return getDubboMapping().group();
    }

    public String getVersion() {
        return getDubboMapping().version();
    }

    public String getInterfaceName() {
        String url = getDubboMapping().value();
        String interfaceName = url.substring(0, url.lastIndexOf("."));
        if (StrUtil.isBlank(interfaceName)) {
            throw new IllegalArgumentException("interfaceName is null.");
        }
        return interfaceName;
    }

    public String getMethodName() {
        String url = getDubboMapping().value();
        String methodName = url.substring(url.lastIndexOf(".") + 1);
        if (StrUtil.isBlank(methodName)) {
            throw new IllegalArgumentException("methodName is null");
        }
        return methodName;
    }

    private DubboMapping getDubboMapping() {
        return APIServiceUtil.getAnnotation(invocation.getMethod(), DubboMapping.class);
    }

    @Override
    public Method getMethod() {
        return invocation.getMethod();
    }

    @Override
    public Object[] getArguments() {
        return invocation.getArguments();
    }
}
