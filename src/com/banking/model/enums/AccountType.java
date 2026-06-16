package com.banking.model.enums;

/**
 * Enum representing the types of bank accounts available.
 * Demonstrates the use of enums with fields and constructors.
 */
public enum AccountType {
    SAVINGS("Savings Account", 4.0),
    CURRENT("Current Account", 0.0),
    FIXED_DEPOSIT("Fixed Deposit Account", 7.5);

    private final String displayName;
    private final double defaultInterestRate;

    AccountType(String displayName, double defaultInterestRate) {
        this.displayName = displayName;
        this.defaultInterestRate = defaultInterestRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultInterestRate() {
        return defaultInterestRate;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
