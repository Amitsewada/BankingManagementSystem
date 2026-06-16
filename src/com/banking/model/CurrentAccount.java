package com.banking.model;

import com.banking.model.enums.AccountType;

/**
 * Current Account - designed for businesses with no interest but overdraft facility.
 * 
 * Demonstrates: Inheritance, Method Overriding, Overdraft concept
 */
public class CurrentAccount extends Account {

    private double overdraftLimit;
    private static final double DEFAULT_OVERDRAFT_LIMIT = 50000.00;
    private static final double MIN_BALANCE = 5000.00;

    /**
     * Creates a new Current Account with default overdraft limit.
     */
    public CurrentAccount(String accountNumber, String holderName, double initialDeposit) {
        super(accountNumber, holderName, AccountType.CURRENT, initialDeposit);
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
    }

    /**
     * Overloaded constructor with custom overdraft limit.
     */
    public CurrentAccount(String accountNumber, String holderName, double initialDeposit, double overdraftLimit) {
        this(accountNumber, holderName, initialDeposit);
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }
        this.overdraftLimit = overdraftLimit;
    }

    // ==================== Getters & Setters ====================

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Returns the available balance including overdraft.
     */
    public double getAvailableBalance() {
        return getBalance() + overdraftLimit;
    }

    // ==================== Overridden Methods ====================

    /**
     * Current accounts do not earn interest.
     */
    @Override
    public double calculateInterest() {
        return 0.0;
    }

    /**
     * Minimum balance for current account can go negative up to overdraft limit.
     */
    @Override
    public double getMinimumBalance() {
        return -overdraftLimit; // Allows negative balance up to overdraft limit
    }

    @Override
    public void displayAccountSpecificDetails() {
        System.out.printf("║  Overdraft Limit : ₹ %-26.2f ║%n", overdraftLimit);
        System.out.printf("║  Available Bal.  : ₹ %-26.2f ║%n", getAvailableBalance());
        System.out.printf("║  Interest Rate   : %-28s ║%n", "0.00% (No Interest)");
    }
}
