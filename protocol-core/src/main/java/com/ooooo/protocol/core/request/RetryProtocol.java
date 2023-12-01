package com.ooooo.protocol.core.request;

import cn.hutool.core.map.MapUtil;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.beans.ProtocolWrapper;
import com.ooooo.protocol.core.constants.ProtocolWrapperOrder;
import com.ooooo.protocol.core.context.APIServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
@Order(ProtocolWrapperOrder.RETRY)
public class RetryProtocol extends ProtocolWrapper {

    private static final String RETRIES = "retries";

    private static final String ADD_ON = ".*";

    public RetryProtocol(Protocol protocol) {
        super(protocol);
    }


    @Override
    public Object execute(Invocation invocation) throws Throwable {
        int retry = getRetryTimes(invocation);

        for (int i = 0; i < retry - 1; i++) {
            try {
                return protocol.execute(invocation);
            } catch (Throwable e) {
                log.warn("retry {} times to execute ", i + 1, e);
            }
        }
        return protocol.execute(invocation);
    }


    private int getRetryTimes(Invocation invocation) {
        Map<String, Object> config = APIServiceContext.getProtocolProperties().getConfig();
        if (!config.containsKey(RETRIES)) {
            return 1;
        }

        Map<String, Object> retries = (Map<String, Object>) config.get(RETRIES);
        Method method = invocation.getMethod();

        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();

        // 指定方法
        String key = className + "." + methodName;
        int retryTimes = MapUtil.getInt(retries, key, 0);
        if (retryTimes > 0) {
            return retryTimes;
        }

        while (!className.isEmpty()) {
            key = className + ADD_ON;
            retryTimes = MapUtil.getInt(retries, key, 0);
            if (retryTimes > 0) {
                return retryTimes;
            }
            className = ClassUtils.getPackageName(className);
        }

        return 1;
    }
}
