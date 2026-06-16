package com.banking.model;

import com.banking.model.enums.AccountType;

/**
 * Fixed Deposit Account - locked funds with higher interest rate.
 * 
 * Demonstrates: Inheritance, Method Overriding, Business logic encapsulation
 */
public class FixedDepositAccount extends Account {

    private double interestRate;
    private int tenureMonths;
    private double maturityAmount;
    private boolean isMature;

    /**
     * Creates a new Fixed Deposit Account.
     *
     * @param accountNumber  unique account number
     * @param holderName     name of account holder
     * @param depositAmount  fixed deposit amount (locked)
     * @param tenureMonths   tenure in months
     */
    public FixedDepositAccount(String accountNumber, String holderName, double depositAmount, int tenureMonths) {
        super(accountNumber, holderName, AccountType.FIXED_DEPOSIT, depositAmount);
        if (depositAmount < 10000) {
            throw new IllegalArgumentException("Minimum FD amount is ₹10,000.");
        }
        if (tenureMonths < 6 || tenureMonths > 120) {
            throw new IllegalArgumentException("FD tenure must be between 6 and 120 months.");
        }
        this.interestRate = AccountType.FIXED_DEPOSIT.getDefaultInterestRate();
        this.tenureMonths = tenureMonths;
        this.isMature = false;
        this.maturityAmount = calculateMaturityAmount();
    }

    // ==================== Getters ====================

    public double getInterestRate() {
        return interestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public double getMaturityAmount() {
        return maturityAmount;
    }

    public boolean isMature() {
        return isMature;
    }

    public void setMature(boolean mature) {
        isMature = mature;
    }

    // ==================== Business Logic ====================

    /**
     * Calculates the maturity amount using compound interest formula.
     * A = P(1 + r/n)^(n*t)
     * Where n = 4 (quarterly compounding), t = tenure in years
     */
    private double calculateMaturityAmount() {
        double principal = getBalance();
        double rate = interestRate / 100.0;
        int compoundingFrequency = 4; // Quarterly
        double tenureYears = tenureMonths / 12.0;

        return principal * Math.pow(1 + rate / compoundingFrequency,
                compoundingFrequency * tenureYears);
    }

    /**
     * Calculates annual interest earned.
     */
    @Override
    public double calculateInterest() {
        return (getBalance() * interestRate) / 100.0;
    }

    /**
     * FD does not allow withdrawal before maturity (returns full balance as min).
     */
    @Override
    public double getMinimumBalance() {
        if (!isMature) {
            return getBalance(); // Cannot withdraw before maturity
        }
        return 0;
    }

    /**
     * Overrides debit to prevent premature withdrawal.
     */
    @Override
    public boolean debit(double amount, String description) {
        if (!isMature) {
            System.out.println("⚠ Fixed Deposit has not matured yet. Premature withdrawal not allowed.");
            System.out.printf("  Maturity Amount: ₹%.2f | Tenure: %d months%n", maturityAmount, tenureMonths);
            return false;
        }
        return super.debit(amount, description);
    }

    @Override
    public void displayAccountSpecificDetails() {
        System.out.printf("║  Interest Rate   : %-27s%% ║%n", String.format("%.2f", interestRate));
        System.out.printf("║  Tenure          : %-25s mos ║%n", tenureMonths);
        System.out.printf("║  Maturity Amt    : ₹ %-26.2f ║%n", maturityAmount);
        System.out.printf("║  Maturity Status : %-28s ║%n", isMature ? "✅ Matured" : "⏳ Not Matured");
    }
}
