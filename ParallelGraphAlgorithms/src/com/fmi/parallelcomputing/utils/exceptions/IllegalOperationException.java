package com.fmi.parallelcomputing.utils.exceptions;

/**
 * Thrown when operation that is forbidden from execution is trying to take place.
 */
public class IllegalOperationException extends RuntimeException {

    public IllegalOperationException(String message) {
        super(message);
    }
}
