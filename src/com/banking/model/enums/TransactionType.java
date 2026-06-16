package com.banking.model.enums;

/**
 * Enum representing the types of transactions that can be performed.
 */
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER_IN("Transfer In"),
    TRANSFER_OUT("Transfer Out"),
    INTEREST_CREDIT("Interest Credit"),
    LOAN_DISBURSEMENT("Loan Disbursement"),
    LOAN_EMI_PAYMENT("Loan EMI Payment");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
