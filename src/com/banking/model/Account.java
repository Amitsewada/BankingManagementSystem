package com.banking.model;

import com.banking.model.enums.AccountType;
import com.banking.model.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all types of bank accounts.
 * 
 * Demonstrates:
 * - Abstraction (abstract class with abstract methods)
 * - Encapsulation (private fields, controlled access)
 * - Polymorphism (abstract methods overridden by subclasses)
 */
public abstract class Account {

    private String accountNumber;
    private String holderName;
    private double balance;
    private AccountType accountType;
    private LocalDateTime createdDate;
    private boolean isActive;
    private List<Transaction> transactionHistory;
    private int transactionCounter;

    // ==================== Constructor ====================

    /**
     * Protected constructor - can only be called by subclasses.
     *
     * @param accountNumber unique account number
     * @param holderName    name of the account holder
     * @param accountType   type of account
     * @param initialDeposit initial deposit amount
     */
    protected Account(String accountNumber, String holderName, AccountType accountType, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = initialDeposit;
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
        this.transactionHistory = new ArrayList<>();
        this.transactionCounter = 0;

        // Record the initial deposit as a transaction
        if (initialDeposit > 0) {
            addTransaction(TransactionType.DEPOSIT, initialDeposit, "Initial Deposit");
        }
    }

    // ==================== Abstract Methods ====================

    /**
     * Calculate the interest for this account.
     * Each account type has its own interest calculation logic.
     *
     * @return calculated interest amount
     */
    public abstract double calculateInterest();

    /**
     * Get the minimum balance required for this account type.
     *
     * @return minimum balance amount
     */
    public abstract double getMinimumBalance();

    /**
     * Display account-specific details.
     */
    public abstract void displayAccountSpecificDetails();

    // ==================== Encapsulated Getters & Setters ====================

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Holder name cannot be null or empty.");
        }
        this.holderName = holderName.trim();
    }

    public double getBalance() {
        return balance;
    }

    // Balance is not directly settable - only through deposit/withdraw
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory); // Defensive copy
    }

    // ==================== Core Banking Operations ====================

    /**
     * Credits the given amount to the account.
     * Demonstrates method that can be overridden by subclasses.
     *
     * @param amount      amount to deposit
     * @param description narration for the transaction
     */
    public void credit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        if (!isActive) {
            throw new IllegalStateException("Cannot perform operations on an inactive account.");
        }
        this.balance += amount;
        addTransaction(TransactionType.DEPOSIT, amount, description);
    }

    /**
     * Debits the given amount from the account.
     * Checks for minimum balance before allowing withdrawal.
     *
     * @param amount      amount to withdraw
     * @param description narration for the transaction
     * @return true if withdrawal is successful
     */
    public boolean debit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (!isActive) {
            throw new IllegalStateException("Cannot perform operations on an inactive account.");
        }
        if (this.balance - amount < getMinimumBalance()) {
            return false; // Insufficient balance
        }
        this.balance -= amount;
        addTransaction(TransactionType.WITHDRAWAL, amount, description);
        return true;
    }

    // ==================== Transaction Management ====================

    /**
     * Adds a transaction record to the history.
     *
     * @param type        type of transaction
     * @param amount      amount involved
     * @param description narration
     */
    protected void addTransaction(TransactionType type, double amount, String description) {
        transactionCounter++;
        String txnId = "TXN" + String.format("%06d", transactionCounter);
        Transaction txn = new Transaction(txnId, this.accountNumber, type, amount, this.balance, description);
        this.transactionHistory.add(txn);
    }

    /**
     * Returns the last N transactions (mini statement).
     *
     * @param count number of recent transactions to return
     * @return list of recent transactions
     */
    public List<Transaction> getMiniStatement(int count) {
        int size = transactionHistory.size();
        if (count >= size) {
            return new ArrayList<>(transactionHistory);
        }
        return new ArrayList<>(transactionHistory.subList(size - count, size));
    }

    // ==================== Display Methods ====================

    /**
     * Displays full account details.
     * Uses Template Method pattern - calls abstract displayAccountSpecificDetails().
     */
    public void displayAccountDetails() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║              ACCOUNT DETAILS                    ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  Account Number  : %-28s ║%n", accountNumber);
        System.out.printf("║  Holder Name     : %-28s ║%n", holderName);
        System.out.printf("║  Account Type    : %-28s ║%n", accountType.getDisplayName());
        System.out.printf("║  Balance         : ₹ %-26.2f ║%n", balance);
        System.out.printf("║  Status          : %-28s ║%n", isActive ? "Active" : "Inactive");
        System.out.printf("║  Opened On       : %-28s ║%n",
                createdDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        displayAccountSpecificDetails(); // Polymorphic call
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    // ==================== Object class overrides ====================

    @Override
    public String toString() {
        return String.format("[%s] %s - %s | Balance: ₹%.2f | %s",
                accountType.getDisplayName(), accountNumber, holderName, balance,
                isActive ? "Active" : "Inactive");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}
