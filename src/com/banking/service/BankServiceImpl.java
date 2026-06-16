package com.banking.service;

import com.banking.model.*;
import com.banking.model.enums.AccountType;
import com.banking.model.enums.TransactionType;
import com.banking.exception.*;
import com.banking.util.DatabaseManager;
import com.banking.util.IdGenerator;
import com.banking.util.TransactionLogger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of BankService interface.
 * 
 * Demonstrates: Interface Implementation, Collections (HashMap, ArrayList),
 *               Stream API, Exception Handling, Polymorphism in action
 */
public class BankServiceImpl implements BankService {

    // In-memory storage using Collections
    private Map<String, Customer> customers;      // customerId -> Customer
    private Map<String, Account> accounts;         // accountNumber -> Account

    private IdGenerator idGenerator;
    private TransactionLogger logger;
    private DatabaseManager dbManager;

    public BankServiceImpl() {
        this.customers = new HashMap<>();
        this.accounts = new LinkedHashMap<>(); // Maintains insertion order
        this.idGenerator = IdGenerator.getInstance();
        this.logger = TransactionLogger.getInstance();
        this.dbManager = DatabaseManager.getInstance();
    }

    // ==================== Customer Operations ====================

    @Override
    public Customer registerCustomer(String name, String email, String phone, String panNumber) {
        // Check if PAN already exists
        boolean panExists = customers.values().stream()
                .anyMatch(c -> c.getPanNumber().equalsIgnoreCase(panNumber));
        if (panExists) {
            throw new IllegalArgumentException("A customer with PAN " + panNumber + " already exists.");
        }

        String customerId = idGenerator.generateCustomerId();
        Customer customer = new Customer(customerId, name, email, phone, panNumber);
        customers.put(customerId, customer);

        // Save to SQLite
        dbManager.saveCustomer(customerId, name, email, phone, panNumber);

        logger.logMessage("New customer registered: " + customerId + " - " + name);
        System.out.println("✅ Customer registered successfully! ID: " + customerId);
        return customer;
    }

    @Override
    public Customer findCustomer(String customerId) throws CustomerNotFoundException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }
        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // ==================== Account Operations ====================

    @Override
    public Account openSavingsAccount(String customerId, double initialDeposit)
            throws CustomerNotFoundException, BankingException {
        Customer customer = findCustomer(customerId);

        if (initialDeposit < 1000) {
            throw new BankingException("Minimum initial deposit for Savings Account is ₹1,000.");
        }

        String accountNumber = idGenerator.generateAccountNumber();
        SavingsAccount account = new SavingsAccount(accountNumber, customer.getName(), initialDeposit);

        accounts.put(accountNumber, account);
        customer.addAccount(account);

        // Save to SQLite
        dbManager.saveAccount(accountNumber, customerId, "SAVINGS", account.getBalance());

        logger.logMessage("Savings Account opened: " + accountNumber + " for " + customerId);
        System.out.println("✅ Savings Account opened! Account No: " + accountNumber);
        return account;
    }

    @Override
    public Account openCurrentAccount(String customerId, double initialDeposit, double overdraftLimit)
            throws CustomerNotFoundException, BankingException {
        Customer customer = findCustomer(customerId);

        if (initialDeposit < 5000) {
            throw new BankingException("Minimum initial deposit for Current Account is ₹5,000.");
        }

        String accountNumber = idGenerator.generateAccountNumber();
        CurrentAccount account = new CurrentAccount(accountNumber, customer.getName(), initialDeposit, overdraftLimit);

        accounts.put(accountNumber, account);
        customer.addAccount(account);

        // Save to SQLite
        dbManager.saveAccount(accountNumber, customerId, "CURRENT", account.getBalance());

        logger.logMessage("Current Account opened: " + accountNumber + " for " + customerId);
        System.out.println("✅ Current Account opened! Account No: " + accountNumber);
        return account;
    }

    @Override
    public Account openFixedDepositAccount(String customerId, double depositAmount, int tenureMonths)
            throws CustomerNotFoundException, BankingException {
        Customer customer = findCustomer(customerId);

        if (depositAmount < 10000) {
            throw new BankingException("Minimum FD amount is ₹10,000.");
        }

        String accountNumber = idGenerator.generateAccountNumber();
        FixedDepositAccount account = new FixedDepositAccount(accountNumber, customer.getName(),
                depositAmount, tenureMonths);

        accounts.put(accountNumber, account);
        customer.addAccount(account);

        // Save to SQLite
        dbManager.saveAccount(accountNumber, customerId, "FIXED_DEPOSIT", account.getBalance());

        logger.logMessage("Fixed Deposit opened: " + accountNumber + " for " + customerId);
        System.out.println("✅ Fixed Deposit Account opened! Account No: " + accountNumber);
        return account;
    }

    @Override
    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // ==================== Transaction Operations ====================

    @Override
    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Account account = findAccount(accountNumber);
        account.credit(amount, "Cash Deposit");

        // Log the last transaction
        List<Transaction> txns = account.getTransactionHistory();
        logger.logTransaction(txns.get(txns.size() - 1));

        System.out.printf("✅ ₹%.2f deposited to %s. New Balance: ₹%.2f%n",
                amount, accountNumber, account.getBalance());
    }

    @Override
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InsufficientBalanceException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Account account = findAccount(accountNumber);
        boolean success = account.debit(amount, "Cash Withdrawal");

        if (!success) {
            if (account instanceof FixedDepositAccount && !((FixedDepositAccount) account).isMature()) {
                throw new IllegalArgumentException("Fixed Deposit has not matured yet. Premature withdrawal not allowed.");
            }
            if (account instanceof SavingsAccount && ((SavingsAccount) account).getRemainingWithdrawals() <= 0) {
                throw new IllegalArgumentException("Daily withdrawal limit reached for Savings Account.");
            }
            throw new InsufficientBalanceException(accountNumber, account.getBalance(), amount);
        }

        // Log the last transaction
        List<Transaction> txns = account.getTransactionHistory();
        logger.logTransaction(txns.get(txns.size() - 1));

        System.out.printf("✅ ₹%.2f withdrawn from %s. New Balance: ₹%.2f%n",
                amount, accountNumber, account.getBalance());
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InsufficientBalanceException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account fromAccount = findAccount(fromAccountNumber);
        Account toAccount = findAccount(toAccountNumber);

        // Debit from source
        boolean success = fromAccount.debit(amount, "Transfer to " + toAccountNumber);
        if (!success) {
            if (fromAccount instanceof FixedDepositAccount && !((FixedDepositAccount) fromAccount).isMature()) {
                throw new IllegalArgumentException("Fixed Deposit has not matured yet. Premature withdrawal not allowed.");
            }
            if (fromAccount instanceof SavingsAccount && ((SavingsAccount) fromAccount).getRemainingWithdrawals() <= 0) {
                throw new IllegalArgumentException("Daily withdrawal limit reached for Savings Account.");
            }
            throw new InsufficientBalanceException(fromAccountNumber, fromAccount.getBalance(), amount);
        }

        // Update transaction type for the debit
        List<Transaction> fromTxns = fromAccount.getTransactionHistory();
        logger.logTransaction(fromTxns.get(fromTxns.size() - 1));

        // Credit to destination
        toAccount.credit(amount, "Transfer from " + fromAccountNumber);
        List<Transaction> toTxns = toAccount.getTransactionHistory();
        logger.logTransaction(toTxns.get(toTxns.size() - 1));

        System.out.printf("✅ ₹%.2f transferred from %s to %s%n", amount, fromAccountNumber, toAccountNumber);
        System.out.printf("   Sender Balance: ₹%.2f | Receiver Balance: ₹%.2f%n",
                fromAccount.getBalance(), toAccount.getBalance());
    }

    @Override
    public List<Transaction> getMiniStatement(String accountNumber, int count)
            throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        return account.getMiniStatement(count);
    }

    @Override
    public void calculateAndCreditInterest(String accountNumber) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        double interest = account.calculateInterest();

        if (interest > 0) {
            account.credit(interest, "Interest Credit @ " +
                    (account instanceof SavingsAccount ?
                            ((SavingsAccount) account).getInterestRate() + "%" :
                            account.getAccountType().getDefaultInterestRate() + "%"));

            List<Transaction> txns = account.getTransactionHistory();
            logger.logTransaction(txns.get(txns.size() - 1));

            System.out.printf("✅ Interest of ₹%.2f credited to %s. New Balance: ₹%.2f%n",
                    interest, accountNumber, account.getBalance());
        } else {
            System.out.println("ℹ No interest applicable for this account type.");
        }
    }

    @Override
    public void closeAccount(String accountNumber) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        account.setActive(false);
        logger.logMessage("Account closed: " + accountNumber);
        System.out.println("✅ Account " + accountNumber + " has been closed.");
    }

    // ==================== Utility Methods ====================

    /**
     * Search accounts by holder name (partial match).
     * Demonstrates: Stream API, Lambda expressions
     *
     * @param name partial name to search
     * @return list of matching accounts
     */
    public List<Account> searchAccountsByName(String name) {
        return accounts.values().stream()
                .filter(acc -> acc.getHolderName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Get total deposits across all accounts.
     * Demonstrates: Stream API reduce operation
     *
     * @return total deposits
     */
    public double getTotalDeposits() {
        return accounts.values().stream()
                .filter(Account::isActive)
                .mapToDouble(Account::getBalance)
                .sum();
    }

    /**
     * Get count of accounts by type.
     * Demonstrates: Stream API groupingBy collector
     *
     * @return map of account type to count
     */
    public Map<String, Long> getAccountCountByType() {
        return accounts.values().stream()
                .collect(Collectors.groupingBy(
                        acc -> acc.getAccountType().getDisplayName(),
                        Collectors.counting()
                ));
    }

    /**
     * Delete a customer after verifying all accounts are closed and balances are zero.
     * Demonstrates: Business rule validation, Stream API
     *
     * @param customerId customer to delete
     * @throws CustomerNotFoundException if customer not found
     */
    public void deleteCustomer(String customerId) throws CustomerNotFoundException {
        Customer customer = findCustomer(customerId);

        // Check if customer has any active accounts with balance
        List<Account> customerAccounts = accounts.values().stream()
                .filter(acc -> acc.getHolderName().equals(customer.getName()))
                .collect(Collectors.toList());

        for (Account acc : customerAccounts) {
            if (acc.isActive() && acc.getBalance() > 0) {
                throw new IllegalArgumentException(
                    "Cannot delete customer. Account " + acc.getAccountNumber()
                    + " still has balance ₹" + String.format("%.2f", acc.getBalance())
                    + ". Please close all accounts and settle balances first.");
            }
        }

        // Remove all associated accounts
        for (Account acc : customerAccounts) {
            accounts.remove(acc.getAccountNumber());
        }

        // Remove customer
        customers.remove(customerId);
        dbManager.deleteCustomer(customerId);

        logger.logMessage("Customer deleted: " + customerId + " - " + customer.getName());
        System.out.println("✅ Customer " + customerId + " (" + customer.getName() + ") has been permanently deleted.");
    }

    /**
     * Find the customer ID associated with an account number.
     *
     * @param accountNumber the account number
     * @return customer ID or null
     */
    public String getCustomerIdForAccount(String accountNumber) {
        for (Map.Entry<String, Customer> entry : customers.entrySet()) {
            for (Account acc : entry.getValue().getAccounts()) {
                if (acc.getAccountNumber().equals(accountNumber)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
