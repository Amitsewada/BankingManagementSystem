package com.banking.service;

import com.banking.model.*;
import com.banking.model.enums.LoanType;
import com.banking.exception.*;
import com.banking.util.IdGenerator;
import com.banking.util.TransactionLogger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of LoanService interface.
 * 
 * Demonstrates: Interface Implementation, Factory Method for Loan creation,
 *               Collections, Polymorphism
 */
public class LoanServiceImpl implements LoanService {

    private Map<String, Loan> loans; // loanId -> Loan
    private IdGenerator idGenerator;
    private TransactionLogger logger;

    public LoanServiceImpl() {
        this.loans = new LinkedHashMap<>();
        this.idGenerator = IdGenerator.getInstance();
        this.logger = TransactionLogger.getInstance();
    }

    @Override
    public Loan applyForLoan(String customerId, String linkedAccountNumber, LoanType loanType,
                             double amount, int tenureMonths, double annualIncome) throws BankingException {
        if (amount <= 0) {
            throw new BankingException("Loan amount must be positive.");
        }
        if (tenureMonths <= 0 || tenureMonths > loanType.getMaxTenureYears() * 12) {
            throw new BankingException("Invalid tenure. Maximum allowed: "
                    + loanType.getMaxTenureYears() + " years (" + loanType.getMaxTenureYears() * 12 + " months).");
        }

        String loanId = idGenerator.generateLoanId();
        Loan loan;

        // Factory Method pattern - creates different loan types based on LoanType enum
        switch (loanType) {
            case HOME_LOAN:
                loan = new HomeLoan(loanId, customerId, linkedAccountNumber,
                        amount, tenureMonths, "Property Address TBD", amount * 1.25);
                break;
            case PERSONAL_LOAN:
                loan = new PersonalLoan(loanId, customerId, linkedAccountNumber,
                        amount, tenureMonths, "Personal Expenses");
                break;
            case EDUCATION_LOAN:
                loan = new EducationLoan(loanId, customerId, linkedAccountNumber,
                        amount, tenureMonths, "University TBD", "Course TBD", 4);
                break;
            default:
                throw new BankingException("Unsupported loan type: " + loanType);
        }

        // Check eligibility using polymorphism
        if (!loan.checkEligibility(annualIncome)) {
            System.out.println("⚠ Loan eligibility check FAILED.");
            System.out.printf("  Max allowed for your income: ₹%.2f%n", loan.getMaxLoanAmount(annualIncome));
            throw new BankingException("Loan eligibility check failed. Your income does not meet the criteria.");
        }

        loans.put(loanId, loan);
        logger.logMessage("Loan application submitted: " + loanId + " | Type: "
                + loanType.getDisplayName() + " | Amount: ₹" + String.format("%.2f", amount));

        System.out.println("✅ Loan application submitted successfully!");
        System.out.println("   Loan ID: " + loanId);
        System.out.printf("   EMI: ₹%.2f/month for %d months%n", loan.getEmiAmount(), tenureMonths);
        System.out.printf("   Total Payable: ₹%.2f%n", loan.getTotalAmountPayable());

        return loan;
    }

    @Override
    public void approveLoan(String loanId) throws BankingException {
        Loan loan = findLoan(loanId);
        if (loan.isApproved()) {
            throw new BankingException("Loan " + loanId + " is already approved.");
        }
        loan.approveLoan();
        logger.logMessage("Loan approved: " + loanId);
    }

    @Override
    public void payEMI(String loanId) throws BankingException {
        Loan loan = findLoan(loanId);
        if (!loan.isActive()) {
            throw new BankingException("Loan " + loanId + " is not active. Cannot pay EMI.");
        }

        boolean success = loan.payEMI();
        if (success) {
            System.out.printf("✅ EMI of ₹%.2f paid for loan %s. Remaining EMIs: %d%n",
                    loan.getEmiAmount(), loanId, loan.getRemainingEMIs());
            System.out.printf("   Amount Paid: ₹%.2f / ₹%.2f%n",
                    loan.getAmountPaid(), loan.getTotalAmountPayable());
            logger.logMessage("EMI paid for loan: " + loanId + " | EMI: ₹" +
                    String.format("%.2f", loan.getEmiAmount()));
        }
    }

    @Override
    public List<Loan> getCustomerLoans(String customerId) {
        return loans.values().stream()
                .filter(loan -> loan.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans.values());
    }

    @Override
    public Loan findLoan(String loanId) throws BankingException {
        Loan loan = loans.get(loanId);
        if (loan == null) {
            throw new BankingException("Loan not found: " + loanId);
        }
        return loan;
    }

    /**
     * Settle a loan completely in one payment.
     * Pays all remaining EMIs at once and closes the loan.
     *
     * @param loanId loan ID to settle
     * @return the remaining amount that was paid
     * @throws BankingException if loan not found or not active
     */
    public double settleLoanFull(String loanId) throws BankingException {
        Loan loan = findLoan(loanId);
        if (!loan.isActive()) {
            throw new BankingException("Loan " + loanId + " is not active. Cannot settle.");
        }

        double remaining = loan.getRemainingAmount();
        int remainingEMIs = loan.getRemainingEMIs();

        // Pay all remaining EMIs
        while (loan.isActive()) {
            loan.payEMI();
        }

        logger.logMessage("Loan fully settled: " + loanId + " | Amount Paid: ₹" +
                String.format("%.2f", remaining) + " (" + remainingEMIs + " EMIs)");

        System.out.println("🎉 Loan " + loanId + " has been fully settled!");
        System.out.printf("   Settlement Amount: ₹%.2f (%d EMIs cleared)%n", remaining, remainingEMIs);

        return remaining;
    }
}
