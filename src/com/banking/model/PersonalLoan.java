package com.banking.model;

import com.banking.model.enums.LoanType;

/**
 * Personal Loan - unsecured loan for personal expenses.
 * Demonstrates: Inheritance, Polymorphism
 */
public class PersonalLoan extends Loan {

    private String purpose;

    public PersonalLoan(String loanId, String customerId, String linkedAccountNumber,
                        double principalAmount, int tenureMonths, String purpose) {
        super(loanId, customerId, linkedAccountNumber, LoanType.PERSONAL_LOAN, principalAmount, tenureMonths);
        this.purpose = purpose;
    }

    @Override
    public boolean checkEligibility(double annualIncome) {
        // Personal loan: Max 2x annual income, EMI <= 30% of monthly income
        double monthlyIncome = annualIncome / 12.0;
        return getPrincipalAmount() <= (annualIncome * 2) && getEmiAmount() <= (monthlyIncome * 0.30);
    }

    @Override
    public double getMaxLoanAmount(double annualIncome) {
        return annualIncome * 2;
    }

    @Override
    public void displayLoanSpecificDetails() {
        System.out.printf("║  Purpose         : %-28s ║%n", purpose);
        System.out.printf("║  Collateral      : %-28s ║%n", "Not Required (Unsecured)");
    }
}
