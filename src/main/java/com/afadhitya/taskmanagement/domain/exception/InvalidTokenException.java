package com.afadhitya.taskmanagement.domain.exception;

/**
 * Exception thrown when a token is invalid, expired, or not found.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
