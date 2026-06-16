package com.banking.service;

import com.banking.model.*;
import com.banking.model.enums.LoanType;
import com.banking.exception.*;

import java.util.List;

/**
 * Interface defining loan-related operations.
 * 
 * Demonstrates: Interface (Abstraction)
 */
public interface LoanService {

    /**
     * Apply for a loan.
     *
     * @param customerId          customer ID
     * @param linkedAccountNumber account to link the loan to
     * @param loanType            type of loan
     * @param amount              loan amount
     * @param tenureMonths        loan tenure in months
     * @param annualIncome        customer's annual income for eligibility
     * @return the created Loan object
     * @throws BankingException for validation errors
     */
    Loan applyForLoan(String customerId, String linkedAccountNumber, LoanType loanType,
                      double amount, int tenureMonths, double annualIncome) throws BankingException;

    /**
     * Approve a pending loan.
     *
     * @param loanId loan ID to approve
     * @throws BankingException if loan not found or already processed
     */
    void approveLoan(String loanId) throws BankingException;

    /**
     * Pay an EMI installment for a loan.
     *
     * @param loanId loan ID
     * @throws BankingException if loan not found or inactive
     */
    void payEMI(String loanId) throws BankingException;

    /**
     * Get all loans for a customer.
     *
     * @param customerId customer ID
     * @return list of loans
     */
    List<Loan> getCustomerLoans(String customerId);

    /**
     * Get all loans in the system.
     *
     * @return list of all loans
     */
    List<Loan> getAllLoans();

    /**
     * Find a loan by ID.
     *
     * @param loanId loan ID
     * @return Loan object
     * @throws BankingException if not found
     */
    Loan findLoan(String loanId) throws BankingException;
}
