package com.qminder.burgers.qminder.exception;

public class BurgerErrorException extends RuntimeException {
    public BurgerErrorException(String message) {
        super(message);
    }

    public BurgerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
