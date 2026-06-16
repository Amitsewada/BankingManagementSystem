package com.banking.model;

import com.banking.model.enums.AccountType;

/**
 * Savings Account - earns interest on the balance.
 * 
 * Demonstrates: Inheritance, Method Overriding, Constructor Chaining (super)
 */
public class SavingsAccount extends Account {

    private double interestRate;
    private int withdrawalLimitPerDay;
    private int withdrawalsToday;

    private static final double MIN_BALANCE = 1000.00;
    private static final int DEFAULT_WITHDRAWAL_LIMIT = 5;

    /**
     * Creates a new Savings Account.
     *
     * @param accountNumber  unique account number
     * @param holderName     name of account holder
     * @param initialDeposit initial deposit amount
     */
    public SavingsAccount(String accountNumber, String holderName, double initialDeposit) {
        super(accountNumber, holderName, AccountType.SAVINGS, initialDeposit); // Constructor chaining
        this.interestRate = AccountType.SAVINGS.getDefaultInterestRate();
        this.withdrawalLimitPerDay = DEFAULT_WITHDRAWAL_LIMIT;
        this.withdrawalsToday = 0;
    }

    /**
     * Overloaded constructor with custom interest rate.
     *
     * @param accountNumber  unique account number
     * @param holderName     name of account holder
     * @param initialDeposit initial deposit amount
     * @param interestRate   custom interest rate
     */
    public SavingsAccount(String accountNumber, String holderName, double initialDeposit, double interestRate) {
        this(accountNumber, holderName, initialDeposit);
        this.interestRate = interestRate;
    }

    // ==================== Getters & Setters ====================

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        if (interestRate < 0 || interestRate > 15) {
            throw new IllegalArgumentException("Interest rate must be between 0% and 15%.");
        }
        this.interestRate = interestRate;
    }

    public int getWithdrawalLimitPerDay() {
        return withdrawalLimitPerDay;
    }

    public int getRemainingWithdrawals() {
        return withdrawalLimitPerDay - withdrawalsToday;
    }

    public void resetDailyWithdrawals() {
        this.withdrawalsToday = 0;
    }

    // ==================== Overridden Methods ====================

    /**
     * Calculates interest based on balance and interest rate.
     * Interest = (Balance × Rate) / 100
     */
    @Override
    public double calculateInterest() {
        return (getBalance() * interestRate) / 100.0;
    }

    @Override
    public double getMinimumBalance() {
        return MIN_BALANCE;
    }

    /**
     * Override debit to enforce daily withdrawal limit.
     */
    @Override
    public boolean debit(double amount, String description) {
        if (withdrawalsToday >= withdrawalLimitPerDay) {
            System.out.println("⚠ Daily withdrawal limit (" + withdrawalLimitPerDay + ") reached!");
            return false;
        }
        boolean success = super.debit(amount, description);
        if (success) {
            withdrawalsToday++;
        }
        return success;
    }

    @Override
    public void displayAccountSpecificDetails() {
        System.out.printf("║  Interest Rate   : %-27s%% ║%n", String.format("%.2f", interestRate));
        System.out.printf("║  Min Balance     : ₹ %-26.2f ║%n", MIN_BALANCE);
        System.out.printf("║  Withdrawals     : %d / %-24d ║%n", withdrawalsToday, withdrawalLimitPerDay);
    }
}
