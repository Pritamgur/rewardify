package com.rewardify.exceeption;

public class ReviewValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReviewValidationException(String message) {
        super(message);
    }

    public ReviewValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReviewValidationException(Throwable cause) {
        super(cause);
    }
}
