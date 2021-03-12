package com.compiler;

/**
 * maven operation exception.
 *
 * @author LeoLu
 * @since 2021-03-08
 **/
public class MavenOperationException extends BusinessException {

    public MavenOperationException(String message) {
        super(message);
    }

    public MavenOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
