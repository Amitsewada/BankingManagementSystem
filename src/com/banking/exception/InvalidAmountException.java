package com.banking.exception;

/**
 * Exception thrown when an invalid amount is provided for a transaction.
 */
public class InvalidAmountException extends BankingException {

    public InvalidAmountException(double amount) {
        super("Invalid amount: ₹" + String.format("%.2f", amount) + ". Amount must be positive.");
    }
}
