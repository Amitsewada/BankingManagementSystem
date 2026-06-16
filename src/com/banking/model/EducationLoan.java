package com.banking.model;

import com.banking.model.enums.LoanType;

/**
 * Education Loan - for financing higher education.
 * Demonstrates: Inheritance, Polymorphism, additional business logic
 */
public class EducationLoan extends Loan {

    private String institutionName;
    private String courseName;
    private int courseDurationYears;

    public EducationLoan(String loanId, String customerId, String linkedAccountNumber,
                         double principalAmount, int tenureMonths,
                         String institutionName, String courseName, int courseDurationYears) {
        super(loanId, customerId, linkedAccountNumber, LoanType.EDUCATION_LOAN, principalAmount, tenureMonths);
        this.institutionName = institutionName;
        this.courseName = courseName;
        this.courseDurationYears = courseDurationYears;
    }

    @Override
    public boolean checkEligibility(double annualIncome) {
        // Education loan: More relaxed criteria, based on co-applicant income
        // Max loan depends on course type
        return getPrincipalAmount() <= getMaxLoanAmount(annualIncome);
    }

    @Override
    public double getMaxLoanAmount(double annualIncome) {
        // Domestic: up to ₹10 Lakh, International: up to ₹20 Lakh
        return 2000000; // ₹20 Lakh max
    }

    /**
     * Education loans often have a moratorium period equal to course duration + 1 year.
     *
     * @return moratorium period in months
     */
    public int getMoratoriumPeriod() {
        return (courseDurationYears + 1) * 12;
    }

    @Override
    public void displayLoanSpecificDetails() {
        System.out.printf("║  Institution     : %-28s ║%n",
                institutionName.length() > 28 ? institutionName.substring(0, 25) + "..." : institutionName);
        System.out.printf("║  Course          : %-28s ║%n",
                courseName.length() > 28 ? courseName.substring(0, 25) + "..." : courseName);
        System.out.printf("║  Duration        : %-25d yrs ║%n", courseDurationYears);
        System.out.printf("║  Moratorium      : %-25d mos ║%n", getMoratoriumPeriod());
    }
}
