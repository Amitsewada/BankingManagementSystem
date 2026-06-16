package com.banking.model.enums;

/**
 * Enum representing the types of loans offered by the bank.
 */
public enum LoanType {
    HOME_LOAN("Home Loan", 8.5, 30),
    PERSONAL_LOAN("Personal Loan", 12.0, 5),
    EDUCATION_LOAN("Education Loan", 7.0, 15),
    CAR_LOAN("Car Loan", 9.5, 7);

    private final String displayName;
    private final double defaultInterestRate;
    private final int maxTenureYears;

    LoanType(String displayName, double defaultInterestRate, int maxTenureYears) {
        this.displayName = displayName;
        this.defaultInterestRate = defaultInterestRate;
        this.maxTenureYears = maxTenureYears;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultInterestRate() {
        return defaultInterestRate;
    }

    public int getMaxTenureYears() {
        return maxTenureYears;
    }

    @Override
    public String toString() {
        return displayName + " (Rate: " + defaultInterestRate + "%, Max Tenure: " + maxTenureYears + " yrs)";
    }
}
