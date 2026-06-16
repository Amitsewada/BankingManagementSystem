package com.banking.util;

import com.banking.model.Transaction;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class to log transactions to a file.
 * 
 * Demonstrates: File I/O, try-with-resources, Singleton pattern
 */
public class TransactionLogger {

    private static final String LOG_FILE = "transaction_log.txt";
    private static TransactionLogger instance;

    private TransactionLogger() {
        // Initialize log file with header
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, false))) {
            writer.println("=" .repeat(90));
            writer.println("                    BANKING MANAGEMENT SYSTEM - TRANSACTION LOG");
            writer.println("                    Generated: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            writer.println("=".repeat(90));
            writer.println();
        } catch (IOException e) {
            System.err.println("Warning: Could not initialize transaction log file.");
        }
    }

    public static synchronized TransactionLogger getInstance() {
        if (instance == null) {
            instance = new TransactionLogger();
        }
        return instance;
    }

    /**
     * Logs a transaction to the file.
     *
     * @param transaction the transaction to log
     */
    public void logTransaction(Transaction transaction) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.printf("[%s] %-15s | Account: %s | Type: %-15s | Amount: ₹%.2f | Balance: ₹%.2f | %s%n",
                    transaction.getFormattedTimestamp(),
                    transaction.getTransactionId(),
                    transaction.getAccountNumber(),
                    transaction.getType().getDescription(),
                    transaction.getAmount(),
                    transaction.getBalanceAfterTransaction(),
                    transaction.getDescription());
        } catch (IOException e) {
            System.err.println("Warning: Could not write to transaction log.");
        }
    }

    /**
     * Logs a custom message to the file.
     *
     * @param message the message to log
     */
    public void logMessage(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.printf("[%s] INFO: %s%n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                    message);
        } catch (IOException e) {
            System.err.println("Warning: Could not write to transaction log.");
        }
    }
}
