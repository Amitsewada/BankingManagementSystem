package com.banking.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique account numbers and IDs.
 * Uses Singleton pattern - only one instance exists.
 * 
 * Demonstrates: Singleton Design Pattern, Thread safety with AtomicInteger
 */
public class IdGenerator {

    // Singleton instance
    private static IdGenerator instance;

    private AtomicInteger customerCounter;
    private AtomicInteger accountCounter;
    private AtomicInteger loanCounter;

    // Private constructor prevents external instantiation
    private IdGenerator() {
        customerCounter = new AtomicInteger(1000);
        accountCounter = new AtomicInteger(2000000000);
        loanCounter = new AtomicInteger(5000);
    }

    /**
     * Returns the single instance (Singleton pattern).
     * Thread-safe lazy initialization.
     *
     * @return IdGenerator instance
     */
    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }

    /**
     * Generates a unique Customer ID.
     * Format: CUST1001, CUST1002, ...
     */
    public String generateCustomerId() {
        return "CUST" + customerCounter.incrementAndGet();
    }

    /**
     * Generates a unique Account Number.
     * Format: 10-digit number like 2000000001
     */
    public String generateAccountNumber() {
        return String.valueOf(accountCounter.incrementAndGet());
    }

    /**
     * Generates a unique Loan ID.
     * Format: LOAN5001, LOAN5002, ...
     */
    public String generateLoanId() {
        return "LOAN" + loanCounter.incrementAndGet();
    }
}
