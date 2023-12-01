package com.ooooo.protocol.core.interceptor;


import cn.hutool.core.util.RandomUtil;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.constants.InterceptorType;
import com.ooooo.protocol.core.constants.ProtocolConstants;
import com.ooooo.protocol.core.context.APIMethodConfig;
import com.ooooo.protocol.core.context.APIServiceConfig;
import com.ooooo.protocol.core.context.APIServiceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import static com.alibaba.fastjson.JSON.toJSONString;


/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public final class LogInterceptor implements Interceptor {

    @Override
    public Object invoke(InterceptorInvocation invocation) throws Throwable {
        Object result = null;
        Throwable throwable = null;
        init();
        try {
            result = invocation.proceed();
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            log(invocation, result, throwable);
        }

        return result;
    }


    private void log(Invocation invocation, Object result, Throwable throwable) {
        APIServiceConfig apiServiceConfig = APIServiceContext.getAPIServiceConfig();
        APIMethodConfig apiMethodConfig = APIServiceContext.getAPIMethodConfig();
        String protocolId = apiServiceConfig.getProtocolId();
        String requestId = getRequestId();
        String note = apiMethodConfig.getNote();
        String url = apiMethodConfig.getUrl();
        String cost = getCost();

        String requestLog = String.format("protocolId: %s, requestId: %s, invoke: [%s]->%s, params: %s ", protocolId,
                requestId, note, url, toJSONString(invocation.getArguments()));
        log.info(requestLog);
        String responseLog = String.format("protocolId: %s, requestId: %s, invoke: [%s]<-%s, %s, result: %s",
                protocolId, requestId, note, url, cost, toJSONString(result));
        log.info(responseLog);
    }


    @Override
    public int getOrder() {
        return InterceptorType.LOG.getOrder();
    }


    private void init() {
        // stopWatch
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        APIServiceContext.setAttachment(ProtocolConstants.STOP_WATCH_KEY, stopWatch);

        // requestId
        String requestId = RandomUtil.randomString(6);
        APIServiceContext.setAttachment(ProtocolConstants.REQUEST_ID_KEY, requestId);
    }

    private String getCost() {
        StopWatch stopWatch = APIServiceContext.getAttachment(ProtocolConstants.STOP_WATCH_KEY);
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        return stopWatch.getLastTaskTimeMillis() + "ms";
    }

    private String getRequestId() {
        return APIServiceContext.getAttachment(ProtocolConstants.REQUEST_ID_KEY);
    }
}
