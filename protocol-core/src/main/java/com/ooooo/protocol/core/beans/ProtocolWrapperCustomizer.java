package com.ooooo.protocol.core.beans;

import java.util.List;

public interface ProtocolWrapperCustomizer {

    void customize(List<Class<? extends ProtocolWrapper>> wrapperClasses);
}