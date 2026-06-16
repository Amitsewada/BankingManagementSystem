package com.banking.exception;

/**
 * Base exception class for all banking-related exceptions.
 * Demonstrates: Exception hierarchy, Custom exception base class
 */
public class BankingException extends Exception {

    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
