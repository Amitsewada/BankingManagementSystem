package com.banking.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a bank customer who can hold multiple accounts.
 * 
 * Demonstrates: Encapsulation, Composition (has-a relationship with Account),
 *               Collections usage, Input validation
 */
public class Customer {

    private String customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String panNumber;
    private LocalDate dateOfBirth;
    private List<Account> accounts;
    private static final int MAX_ACCOUNTS = 5;

    /**
     * Creates a new Customer.
     *
     * @param customerId unique customer ID
     * @param name       full name
     * @param email      email address
     * @param phone      phone number
     * @param panNumber  PAN card number
     */
    public Customer(String customerId, String name, String email, String phone, String panNumber) {
        this.customerId = customerId;
        setName(name);
        setEmail(email);
        setPhone(phone);
        setPanNumber(panNumber);
        this.accounts = new ArrayList<>();
    }

    // ==================== Getters & Setters with Validation ====================

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email.toLowerCase().trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("^[6-9]\\d{9}$")) {
            throw new IllegalArgumentException("Invalid Indian phone number. Must be 10 digits starting with 6-9.");
        }
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        if (panNumber == null || !panNumber.matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
            throw new IllegalArgumentException("Invalid PAN number format. Expected: ABCDE1234F");
        }
        this.panNumber = panNumber.toUpperCase();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Customer must be at least 18 years old.");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts); // Defensive copy
    }

    // ==================== Account Management ====================

    /**
     * Adds an account to this customer.
     *
     * @param account the account to add
     * @return true if added successfully
     */
    public boolean addAccount(Account account) {
        if (accounts.size() >= MAX_ACCOUNTS) {
            System.out.println("⚠ Maximum account limit (" + MAX_ACCOUNTS + ") reached for customer: " + name);
            return false;
        }
        if (accounts.contains(account)) {
            System.out.println("⚠ Account already linked to this customer.");
            return false;
        }
        accounts.add(account);
        return true;
    }

    /**
     * Removes an account from this customer.
     *
     * @param accountNumber account number to remove
     * @return true if removed successfully
     */
    public boolean removeAccount(String accountNumber) {
        return accounts.removeIf(acc -> acc.getAccountNumber().equals(accountNumber));
    }

    /**
     * Finds an account by account number.
     *
     * @param accountNumber the account number
     * @return Account if found, null otherwise
     */
    public Account findAccount(String accountNumber) {
        return accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calculates total balance across all accounts.
     *
     * @return total balance
     */
    public double getTotalBalance() {
        return accounts.stream()
                .filter(Account::isActive)
                .mapToDouble(Account::getBalance)
                .sum();
    }

    // ==================== Display ====================

    public void displayCustomerDetails() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║              CUSTOMER PROFILE                   ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  Customer ID   : %-30s ║%n", customerId);
        System.out.printf("║  Name          : %-30s ║%n", name);
        System.out.printf("║  Email         : %-30s ║%n", email);
        System.out.printf("║  Phone         : %-30s ║%n", phone);
        System.out.printf("║  PAN           : %-30s ║%n", panNumber);
        if (dateOfBirth != null) {
            System.out.printf("║  DOB           : %-30s ║%n",
                    dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        System.out.printf("║  Total Balance : ₹ %-28.2f ║%n", getTotalBalance());
        System.out.printf("║  Accounts      : %-30d ║%n", accounts.size());
        System.out.println("╠══════════════════════════════════════════════════╣");

        if (!accounts.isEmpty()) {
            System.out.println("║  LINKED ACCOUNTS:                               ║");
            for (Account acc : accounts) {
                System.out.printf("║    → %s%n", acc.toString());
            }
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | Accounts: %d | Total: ₹%.2f",
                customerId, name, phone, accounts.size(), getTotalBalance());
    }
}
