package com.rewardify.exceeption;

public class ResourceNotExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotExistException(String message) {
        super(message);
    }

    public ResourceNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
