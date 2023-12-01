package com.ooooo.protocol.core.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class APIException extends RuntimeException {

    @Getter
    @Setter
    protected String error_no;

    @Getter
    @Setter
    protected String error_info;

    public APIException(String error_info) {
        this("-1", error_info, null);
    }

    public APIException(String error_no, String error_info) {
        this(error_no, error_info, null);
    }

    public APIException(String error_no, String error_info, Throwable cause) {
        super(String.format("%s:%s", error_no, error_info), cause);
        this.error_no = error_no;
        this.error_info = error_info;
    }

}
