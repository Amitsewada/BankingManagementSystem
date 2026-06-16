package com.banking.exception;

/**
 * Exception thrown when a customer is not found in the system.
 */
public class CustomerNotFoundException extends BankingException {

    private final String customerId;

    public CustomerNotFoundException(String customerId) {
        super("Customer not found with ID: " + customerId);
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }
}
