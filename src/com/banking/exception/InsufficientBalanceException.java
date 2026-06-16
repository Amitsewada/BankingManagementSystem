package com.banking.exception;

/**
 * Custom exception thrown when an account has insufficient balance for a transaction.
 * Demonstrates: Custom checked exception, Exception hierarchy
 */
public class InsufficientBalanceException extends BankingException {

    private final double currentBalance;
    private final double requestedAmount;

    public InsufficientBalanceException(String accountNumber, double currentBalance, double requestedAmount) {
        super("Insufficient balance in account " + accountNumber
                + ". Current: ₹" + String.format("%.2f", currentBalance)
                + ", Requested: ₹" + String.format("%.2f", requestedAmount));
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public double getDeficitAmount() {
        return requestedAmount - currentBalance;
    }
}
