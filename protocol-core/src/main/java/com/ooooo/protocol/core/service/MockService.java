package com.ooooo.protocol.core.service;

import com.ooooo.protocol.core.Invocation;

/**
 * 对请求进行mock
 *
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public interface MockService {

    Object mock(Invocation invocation);

    boolean hasMockData(Invocation invocation);
}
