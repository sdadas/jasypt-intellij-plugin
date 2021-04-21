package com.sdadas.jasypt.helper;

public class EncryptionHelperException extends RuntimeException {

    public EncryptionHelperException() {
        super();
    }

    public EncryptionHelperException(String message) {
        super(message);
    }

    public EncryptionHelperException(String message, Throwable cause) {
        super(message, cause);
    }
}
