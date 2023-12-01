package com.ooooo.protocol.core.exception;

import lombok.Getter;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Getter
public class APIServiceException extends RuntimeException {

    protected String code;

    protected String message;

    public APIServiceException(String message) {
        this("-1", message, null);
    }

    public APIServiceException(String code, String message) {
        this(code, message, null);
    }

    public APIServiceException(String code, String message, Throwable cause) {
        super(String.format("%s:%s", code, message), cause);
        this.code = code;
        this.message = message;
    }

}
