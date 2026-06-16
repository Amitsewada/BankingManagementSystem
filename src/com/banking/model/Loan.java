package com.banking.model;

import com.banking.model.enums.LoanType;

/**
 * Abstract base class for all loan types.
 * 
 * Demonstrates: Abstraction, Encapsulation, Template Method Pattern
 */
public abstract class Loan {

    private String loanId;
    private String customerId;
    private String linkedAccountNumber;
    private LoanType loanType;
    private double principalAmount;
    private double interestRate;
    private int tenureMonths;
    private double emiAmount;
    private double totalAmountPayable;
    private double amountPaid;
    private int emisPaid;
    private boolean isApproved;
    private boolean isActive;

    /**
     * Protected constructor for loan creation.
     */
    protected Loan(String loanId, String customerId, String linkedAccountNumber,
                   LoanType loanType, double principalAmount, int tenureMonths) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.linkedAccountNumber = linkedAccountNumber;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.interestRate = loanType.getDefaultInterestRate();
        this.tenureMonths = tenureMonths;
        this.amountPaid = 0;
        this.emisPaid = 0;
        this.isApproved = false;
        this.isActive = false;

        // Calculate EMI and total payable
        this.emiAmount = calculateEMI();
        this.totalAmountPayable = emiAmount * tenureMonths;
    }

    // ==================== Abstract Methods ====================

    /**
     * Check if the customer is eligible for this loan.
     *
     * @param annualIncome customer's annual income
     * @return true if eligible
     */
    public abstract boolean checkEligibility(double annualIncome);

    /**
     * Get the maximum loan amount allowed for this loan type.
     *
     * @param annualIncome customer's annual income
     * @return maximum allowed loan amount
     */
    public abstract double getMaxLoanAmount(double annualIncome);

    /**
     * Display loan-specific details.
     */
    public abstract void displayLoanSpecificDetails();

    // ==================== EMI Calculation ====================

    /**
     * Calculates EMI using the standard formula:
     * EMI = [P × R × (1+R)^N] / [(1+R)^N - 1]
     * Where P = Principal, R = Monthly interest rate, N = Tenure in months
     *
     * @return calculated EMI amount
     */
    public double calculateEMI() {
        double monthlyRate = interestRate / (12 * 100);
        if (monthlyRate == 0) {
            return principalAmount / tenureMonths;
        }
        double factor = Math.pow(1 + monthlyRate, tenureMonths);
        return (principalAmount * monthlyRate * factor) / (factor - 1);
    }

    // ==================== Loan Operations ====================

    /**
     * Approve the loan application.
     */
    public void approveLoan() {
        this.isApproved = true;
        this.isActive = true;
        System.out.println("✅ Loan " + loanId + " has been APPROVED.");
    }

    /**
     * Pay one EMI installment.
     *
     * @return true if EMI was paid successfully
     */
    public boolean payEMI() {
        if (!isActive) {
            System.out.println("⚠ Loan is not active.");
            return false;
        }
        if (emisPaid >= tenureMonths) {
            System.out.println("✅ All EMIs have already been paid. Loan is closed.");
            isActive = false;
            return false;
        }

        amountPaid += emiAmount;
        emisPaid++;

        if (emisPaid >= tenureMonths) {
            isActive = false;
            System.out.println("🎉 Congratulations! Loan fully paid off!");
        }
        return true;
    }

    /**
     * Get remaining loan amount.
     */
    public double getRemainingAmount() {
        return totalAmountPayable - amountPaid;
    }

    /**
     * Get number of remaining EMIs.
     */
    public int getRemainingEMIs() {
        return tenureMonths - emisPaid;
    }

    // ==================== Getters ====================

    public String getLoanId() { return loanId; }
    public String getCustomerId() { return customerId; }
    public String getLinkedAccountNumber() { return linkedAccountNumber; }
    public LoanType getLoanType() { return loanType; }
    public double getPrincipalAmount() { return principalAmount; }
    public double getInterestRate() { return interestRate; }
    public int getTenureMonths() { return tenureMonths; }
    public double getEmiAmount() { return emiAmount; }
    public double getTotalAmountPayable() { return totalAmountPayable; }
    public double getAmountPaid() { return amountPaid; }
    public int getEmisPaid() { return emisPaid; }
    public boolean isApproved() { return isApproved; }
    public boolean isActive() { return isActive; }

    // ==================== Display ====================

    public void displayLoanDetails() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║               LOAN DETAILS                      ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  Loan ID         : %-28s ║%n", loanId);
        System.out.printf("║  Type            : %-28s ║%n", loanType.getDisplayName());
        System.out.printf("║  Principal       : ₹ %-26.2f ║%n", principalAmount);
        System.out.printf("║  Interest Rate   : %-27.2f%% ║%n", interestRate);
        System.out.printf("║  Tenure          : %-25d mos ║%n", tenureMonths);
        System.out.printf("║  EMI Amount      : ₹ %-26.2f ║%n", emiAmount);
        System.out.printf("║  Total Payable   : ₹ %-26.2f ║%n", totalAmountPayable);
        System.out.printf("║  Amount Paid     : ₹ %-26.2f ║%n", amountPaid);
        System.out.printf("║  EMIs Paid       : %d / %-24d ║%n", emisPaid, tenureMonths);
        System.out.printf("║  Remaining       : ₹ %-26.2f ║%n", getRemainingAmount());
        System.out.printf("║  Status          : %-28s ║%n",
                isActive ? "🟢 Active" : (isApproved ? "✅ Closed" : "⏳ Pending"));
        displayLoanSpecificDetails();
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | ₹%.2f | EMI: ₹%.2f | %d/%d paid | %s",
                loanId, loanType.getDisplayName(), principalAmount,
                emiAmount, emisPaid, tenureMonths,
                isActive ? "Active" : "Closed");
    }
}
