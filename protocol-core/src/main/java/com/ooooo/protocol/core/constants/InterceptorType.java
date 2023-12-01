package com.ooooo.protocol.core.constants;

import com.ooooo.protocol.core.context.APIServiceContextInterceptor;
import com.ooooo.protocol.core.interceptor.InterfaceDefaultMethodInterceptor;
import com.ooooo.protocol.core.interceptor.LogInterceptor;
import com.ooooo.protocol.core.interceptor.ProtocolExecuteInterceptor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public enum InterceptorType {

    /**
     * @see InterfaceDefaultMethodInterceptor
     */
    INTERFACE_DEFAULT_METHOD,

    /**
     * @see APIServiceContextInterceptor
     */
    API_SERVICE_CONTEXT,

    /**
     * 用于调试，日志
     *
     * @see LogInterceptor
     */
    LOG,

    /**
     * 执行
     *
     * @see ProtocolExecuteInterceptor
     */
    PROTOCOL,

    ;

    @Getter
    final int order;

    InterceptorType() {
        this.order = this.ordinal() * 100;
    }

}
