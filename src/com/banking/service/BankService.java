package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import com.banking.exception.*;

import java.util.List;

/**
 * Interface defining core banking operations.
 * 
 * Demonstrates: Interface (Abstraction), Contract-based design
 */
public interface BankService {

    // ==================== Customer Operations ====================

    /**
     * Registers a new customer in the system.
     *
     * @param name      full name
     * @param email     email address
     * @param phone     phone number (10-digit Indian)
     * @param panNumber PAN card number
     * @return the created Customer object
     */
    Customer registerCustomer(String name, String email, String phone, String panNumber);

    /**
     * Finds a customer by their ID.
     *
     * @param customerId the customer ID
     * @return Customer object
     * @throws CustomerNotFoundException if not found
     */
    Customer findCustomer(String customerId) throws CustomerNotFoundException;

    /**
     * Returns all registered customers.
     *
     * @return list of all customers
     */
    List<Customer> getAllCustomers();

    // ==================== Account Operations ====================

    /**
     * Opens a new Savings Account for a customer.
     *
     * @param customerId     customer ID
     * @param initialDeposit initial deposit amount
     * @return the created Account
     * @throws CustomerNotFoundException if customer not found
     * @throws BankingException          for other errors
     */
    Account openSavingsAccount(String customerId, double initialDeposit)
            throws CustomerNotFoundException, BankingException;

    /**
     * Opens a new Current Account for a customer.
     *
     * @param customerId     customer ID
     * @param initialDeposit initial deposit amount
     * @param overdraftLimit overdraft limit
     * @return the created Account
     */
    Account openCurrentAccount(String customerId, double initialDeposit, double overdraftLimit)
            throws CustomerNotFoundException, BankingException;

    /**
     * Opens a new Fixed Deposit Account for a customer.
     *
     * @param customerId    customer ID
     * @param depositAmount FD amount
     * @param tenureMonths  FD tenure in months
     * @return the created Account
     */
    Account openFixedDepositAccount(String customerId, double depositAmount, int tenureMonths)
            throws CustomerNotFoundException, BankingException;

    /**
     * Finds an account by account number.
     *
     * @param accountNumber the account number
     * @return Account object
     * @throws AccountNotFoundException if not found
     */
    Account findAccount(String accountNumber) throws AccountNotFoundException;

    /**
     * Returns all accounts in the system.
     *
     * @return list of all accounts
     */
    List<Account> getAllAccounts();

    // ==================== Transaction Operations ====================

    /**
     * Deposits money into an account.
     *
     * @param accountNumber target account number
     * @param amount        deposit amount
     * @throws AccountNotFoundException if account not found
     * @throws InvalidAmountException   if amount is invalid
     */
    void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException;

    /**
     * Withdraws money from an account.
     *
     * @param accountNumber source account number
     * @param amount        withdrawal amount
     * @throws AccountNotFoundException      if account not found
     * @throws InsufficientBalanceException   if insufficient balance
     * @throws InvalidAmountException         if amount is invalid
     */
    void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InsufficientBalanceException, InvalidAmountException;

    /**
     * Transfers money between two accounts.
     *
     * @param fromAccountNumber source account number
     * @param toAccountNumber   destination account number
     * @param amount            transfer amount
     * @throws AccountNotFoundException      if any account not found
     * @throws InsufficientBalanceException   if insufficient balance
     * @throws InvalidAmountException         if amount is invalid
     */
    void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InsufficientBalanceException, InvalidAmountException;

    /**
     * Returns mini statement (recent transactions) for an account.
     *
     * @param accountNumber the account number
     * @param count         number of recent transactions
     * @return list of recent transactions
     * @throws AccountNotFoundException if account not found
     */
    List<Transaction> getMiniStatement(String accountNumber, int count)
            throws AccountNotFoundException;

    /**
     * Calculates and credits interest for a savings account.
     *
     * @param accountNumber the savings account number
     * @throws AccountNotFoundException if account not found
     */
    void calculateAndCreditInterest(String accountNumber) throws AccountNotFoundException;

    /**
     * Closes an account.
     *
     * @param accountNumber the account number to close
     * @throws AccountNotFoundException if not found
     */
    void closeAccount(String accountNumber) throws AccountNotFoundException;
}
