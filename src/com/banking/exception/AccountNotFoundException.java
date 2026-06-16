package com.banking.exception;

/**
 * Exception thrown when an account is not found in the system.
 */
public class AccountNotFoundException extends BankingException {

    private final String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
