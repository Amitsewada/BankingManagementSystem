package com.banking.model;

import com.banking.model.enums.TransactionType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single banking transaction.
 * Implements Comparable for sorting transactions by date.
 * 
 * Demonstrates: Encapsulation, Comparable interface, Immutable object pattern
 */
public class Transaction implements Comparable<Transaction> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String transactionId;
    private final String accountNumber;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfterTransaction;
    private final LocalDateTime timestamp;
    private final String description;

    /**
     * Constructor to create a new Transaction.
     *
     * @param transactionId           unique ID for this transaction
     * @param accountNumber           account number involved
     * @param type                    type of transaction
     * @param amount                  amount involved
     * @param balanceAfterTransaction account balance after this transaction
     * @param description             optional description/narration
     */
    public Transaction(String transactionId, String accountNumber, TransactionType type,
                       double amount, double balanceAfterTransaction, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    // ==================== Getters (No setters - Immutable) ====================

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    // ==================== Comparable Implementation ====================

    @Override
    public int compareTo(Transaction other) {
        return other.timestamp.compareTo(this.timestamp); // Most recent first
    }

    // ==================== Display ====================

    @Override
    public String toString() {
        String sign = (type == TransactionType.WITHDRAWAL || type == TransactionType.TRANSFER_OUT
                || type == TransactionType.LOAN_EMI_PAYMENT) ? "-" : "+";

        return String.format("| %-12s | %-20s | %s %-12.2f | %-12.2f | %-25s | %s |",
                transactionId,
                type.getDescription(),
                sign, amount,
                balanceAfterTransaction,
                description,
                getFormattedTimestamp());
    }

    /**
     * Returns a formatted header for transaction table display.
     */
    public static String getTableHeader() {
        String separator = "+" + "-".repeat(14) + "+" + "-".repeat(22) + "+"
                + "-".repeat(15) + "+" + "-".repeat(14) + "+" + "-".repeat(27) + "+" + "-".repeat(22) + "+";
        String header = String.format("| %-12s | %-20s | %-13s | %-12s | %-25s | %-20s |",
                "TXN ID", "TYPE", "AMOUNT", "BALANCE", "DESCRIPTION", "TIMESTAMP");
        return separator + "\n" + header + "\n" + separator;
    }

    public static String getTableFooter() {
        return "+" + "-".repeat(14) + "+" + "-".repeat(22) + "+"
                + "-".repeat(15) + "+" + "-".repeat(14) + "+" + "-".repeat(27) + "+" + "-".repeat(22) + "+";
    }
}
