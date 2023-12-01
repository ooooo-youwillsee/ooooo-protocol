package com.ooooo.protocol.core.service;

import com.ooooo.protocol.core.Invocation;

/**
 * 保存trace信息
 *
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public interface TraceService {

    void invokeBefore(Invocation invocation) throws Throwable;

    void invokeAfter(Invocation invocation, Object result, Throwable throwable) throws Throwable;
}
