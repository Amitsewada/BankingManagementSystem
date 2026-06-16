package com.banking.model;

import com.banking.model.enums.LoanType;

/**
 * Home Loan - for purchasing or constructing residential property.
 * Demonstrates: Inheritance, Method Overriding
 */
public class HomeLoan extends Loan {

    private String propertyAddress;
    private double propertyValue;

    public HomeLoan(String loanId, String customerId, String linkedAccountNumber,
                    double principalAmount, int tenureMonths, String propertyAddress, double propertyValue) {
        super(loanId, customerId, linkedAccountNumber, LoanType.HOME_LOAN, principalAmount, tenureMonths);
        this.propertyAddress = propertyAddress;
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean checkEligibility(double annualIncome) {
        // Home loan: Max 60% of property value, and EMI should be <= 40% of monthly income
        double maxLoan = propertyValue * 0.80;
        double monthlyIncome = annualIncome / 12.0;
        return getPrincipalAmount() <= maxLoan && getEmiAmount() <= (monthlyIncome * 0.40);
    }

    @Override
    public double getMaxLoanAmount(double annualIncome) {
        return Math.min(propertyValue * 0.80, annualIncome * 8);
    }

    @Override
    public void displayLoanSpecificDetails() {
        System.out.printf("║  Property Addr   : %-28s ║%n",
                propertyAddress.length() > 28 ? propertyAddress.substring(0, 25) + "..." : propertyAddress);
        System.out.printf("║  Property Value  : ₹ %-26.2f ║%n", propertyValue);
        System.out.printf("║  LTV Ratio       : %-27.1f%% ║%n",
                (getPrincipalAmount() / propertyValue) * 100);
    }
}
