package com.ooooo.protocol.core.constants;

import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.annotation.APIMapping;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public interface ProtocolConstants {

    String PREFIX_OOOOO = "ooooo";

    String ARG_NAME_ARGUMENTS = "arguments";

    String ARG_NAME_ATTACHMENTS = "attachments";

    /**
     * APIMapping 注解的value为 '*' ，表明为泛化调用
     *
     * @see APIMapping
     */
    String GENERIC_SERVICE_URL_VALUE = "*";

    String REQUEST_ID_KEY = "_REQUEST_ID";

    String REQUEST_LOG_KEY = "_REQUEST_LOG";

    String RESPONSE_LOG_KEY = "_RESPONSE_LOG";

    String STOP_WATCH_KEY = "_STOP_WATCH";

}
