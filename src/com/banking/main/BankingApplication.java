package com.banking.main;

import com.banking.model.*;
import com.banking.model.enums.LoanType;
import com.banking.exception.*;
import com.banking.service.*;
import com.banking.util.TransactionLogger;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main entry point for the Banking Management System.
 * Provides a menu-driven console interface.
 * 
 * Demonstrates: Modular application design, User interaction,
 *               Exception handling in practice, Polymorphism in action
 * 
 * @author Kavyansh Khandelwal
 * @version 1.0
 */
public class BankingApplication {

    public static BankService bankService = new BankServiceImpl();
    public static LoanService loanService = new LoanServiceImpl();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printWelcomeBanner();
        
        try {
            BankingApiServer.startServer();
        } catch (Exception e) {
            System.err.println("Failed to start Web Server: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:  customerMenu(); break;
                    case 2:  accountMenu(); break;
                    case 3:  transactionMenu(); break;
                    case 4:  loanMenu(); break;
                    case 5:  bankSummary(); break;
                    case 0:
                        running = false;
                        System.out.println("\n🙏 Thank you for using Banking Management System!");
                        System.out.println("   Transaction log saved to: transaction_log.txt");
                        break;
                    default:
                        System.out.println("⚠ Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // ======================== MAIN MENU ========================

    private static void printWelcomeBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║          🏦  BANKING MANAGEMENT SYSTEM  🏦                  ║");
        System.out.println("║                                                              ║");
        System.out.println("║          A Java OOP Console Application                      ║");
        System.out.println("║          Version 1.0                                         ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           MAIN MENU                  ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  1.  Customer Management           ║");
        System.out.println("║  2.  Account Management            ║");
        System.out.println("║  3.  Transactions                  ║");
        System.out.println("║  4.  Loan Management               ║");
        System.out.println("║  5.  Bank Summary                  ║");
        System.out.println("║  0.  Exit                          ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ======================== CUSTOMER MENU ========================

    private static void customerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║       CUSTOMER MANAGEMENT            ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Register New Customer            ║");
            System.out.println("║  2. View Customer Details            ║");
            System.out.println("║  3. List All Customers               ║");
            System.out.println("║  0. Back to Main Menu                ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = getIntInput("Enter your choice: ");
            try {
                switch (choice) {
                    case 1: registerCustomer(); break;
                    case 2: viewCustomer(); break;
                    case 3: listAllCustomers(); break;
                    case 0: back = true; break;
                    default: System.out.println("⚠ Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private static void registerCustomer() {
        System.out.println("\n--- Register New Customer ---");
        String name = getStringInput("Full Name: ");
        String email = getStringInput("Email: ");
        String phone = getStringInput("Phone (10-digit): ");
        String pan = getStringInput("PAN Number (e.g., ABCDE1234F): ");

        Customer customer = bankService.registerCustomer(name, email, phone, pan);
        customer.displayCustomerDetails();
    }

    private static void viewCustomer() throws CustomerNotFoundException {
        String customerId = getStringInput("Enter Customer ID: ").toUpperCase();
        Customer customer = bankService.findCustomer(customerId);
        customer.displayCustomerDetails();
    }

    private static void listAllCustomers() {
        List<Customer> customers = bankService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("ℹ No customers registered yet.");
            return;
        }
        System.out.println("\n--- All Customers (" + customers.size() + ") ---");
        for (Customer c : customers) {
            System.out.println("  " + c);
        }
    }

    // ======================== ACCOUNT MENU ========================

    private static void accountMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║       ACCOUNT MANAGEMENT             ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Open Savings Account             ║");
            System.out.println("║  2. Open Current Account             ║");
            System.out.println("║  3. Open Fixed Deposit               ║");
            System.out.println("║  4. View Account Details             ║");
            System.out.println("║  5. List All Accounts                ║");
            System.out.println("║  6. Calculate Interest               ║");
            System.out.println("║  7. Close Account                    ║");
            System.out.println("║  0. Back to Main Menu                ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = getIntInput("Enter your choice: ");
            try {
                switch (choice) {
                    case 1: openSavingsAccount(); break;
                    case 2: openCurrentAccount(); break;
                    case 3: openFixedDeposit(); break;
                    case 4: viewAccount(); break;
                    case 5: listAllAccounts(); break;
                    case 6: calculateInterest(); break;
                    case 7: closeAccount(); break;
                    case 0: back = true; break;
                    default: System.out.println("⚠ Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private static void openSavingsAccount() throws CustomerNotFoundException, BankingException {
        String customerId = getStringInput("Customer ID: ").toUpperCase();
        double deposit = getDoubleInput("Initial Deposit (min ₹1,000): ");
        Account account = bankService.openSavingsAccount(customerId, deposit);
        account.displayAccountDetails();
    }

    private static void openCurrentAccount() throws CustomerNotFoundException, BankingException {
        String customerId = getStringInput("Customer ID: ").toUpperCase();
        double deposit = getDoubleInput("Initial Deposit (min ₹5,000): ");
        double overdraft = getDoubleInput("Overdraft Limit: ");
        Account account = bankService.openCurrentAccount(customerId, deposit, overdraft);
        account.displayAccountDetails();
    }

    private static void openFixedDeposit() throws CustomerNotFoundException, BankingException {
        String customerId = getStringInput("Customer ID: ").toUpperCase();
        double amount = getDoubleInput("FD Amount (min ₹10,000): ");
        int tenure = getIntInput("Tenure in months (6-120): ");
        Account account = bankService.openFixedDepositAccount(customerId, amount, tenure);
        account.displayAccountDetails();
    }

    private static void viewAccount() throws AccountNotFoundException {
        String accNo = getStringInput("Account Number: ");
        Account account = bankService.findAccount(accNo);
        account.displayAccountDetails();
    }

    private static void listAllAccounts() {
        List<Account> accounts = bankService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("ℹ No accounts opened yet.");
            return;
        }
        System.out.println("\n--- All Accounts (" + accounts.size() + ") ---");
        for (Account acc : accounts) {
            System.out.println("  " + acc);
        }
    }

    private static void calculateInterest() throws AccountNotFoundException {
        String accNo = getStringInput("Account Number: ");
        bankService.calculateAndCreditInterest(accNo);
    }

    private static void closeAccount() throws AccountNotFoundException {
        String accNo = getStringInput("Account Number to close: ");
        bankService.closeAccount(accNo);
    }

    // ======================== TRANSACTION MENU ========================

    private static void transactionMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║         TRANSACTIONS                 ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Deposit Money                    ║");
            System.out.println("║  2. Withdraw Money                   ║");
            System.out.println("║  3. Transfer Money                   ║");
            System.out.println("║  4. Mini Statement                   ║");
            System.out.println("║  5. Check Balance                    ║");
            System.out.println("║  0. Back to Main Menu                ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = getIntInput("Enter your choice: ");
            try {
                switch (choice) {
                    case 1: depositMoney(); break;
                    case 2: withdrawMoney(); break;
                    case 3: transferMoney(); break;
                    case 4: miniStatement(); break;
                    case 5: checkBalance(); break;
                    case 0: back = true; break;
                    default: System.out.println("⚠ Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private static void depositMoney() throws AccountNotFoundException, InvalidAmountException {
        String accNo = getStringInput("Account Number: ");
        double amount = getDoubleInput("Deposit Amount: ₹");
        bankService.deposit(accNo, amount);
    }

    private static void withdrawMoney() throws AccountNotFoundException,
            InsufficientBalanceException, InvalidAmountException {
        String accNo = getStringInput("Account Number: ");
        double amount = getDoubleInput("Withdrawal Amount: ₹");
        bankService.withdraw(accNo, amount);
    }

    private static void transferMoney() throws AccountNotFoundException,
            InsufficientBalanceException, InvalidAmountException {
        String fromAcc = getStringInput("From Account: ");
        String toAcc = getStringInput("To Account: ");
        double amount = getDoubleInput("Transfer Amount: ₹");
        bankService.transfer(fromAcc, toAcc, amount);
    }

    private static void miniStatement() throws AccountNotFoundException {
        String accNo = getStringInput("Account Number: ");
        int count = getIntInput("Number of recent transactions (default 10): ");
        if (count <= 0) count = 10;

        List<Transaction> transactions = bankService.getMiniStatement(accNo, count);
        if (transactions.isEmpty()) {
            System.out.println("ℹ No transactions found.");
            return;
        }

        System.out.println("\n--- Mini Statement for Account: " + accNo + " ---");
        System.out.println(Transaction.getTableHeader());
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        System.out.println(Transaction.getTableFooter());
    }

    private static void checkBalance() throws AccountNotFoundException {
        String accNo = getStringInput("Account Number: ");
        Account account = bankService.findAccount(accNo);
        System.out.printf("%n💰 Account: %s | Balance: ₹%.2f | Type: %s%n",
                accNo, account.getBalance(), account.getAccountType().getDisplayName());
    }

    // ======================== LOAN MENU ========================

    private static void loanMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║         LOAN MANAGEMENT              ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Apply for Loan                   ║");
            System.out.println("║  2. Approve Loan                     ║");
            System.out.println("║  3. Pay EMI                          ║");
            System.out.println("║  4. View Loan Details                ║");
            System.out.println("║  5. List All Loans                   ║");
            System.out.println("║  0. Back to Main Menu                ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = getIntInput("Enter your choice: ");
            try {
                switch (choice) {
                    case 1: applyForLoan(); break;
                    case 2: approveLoan(); break;
                    case 3: payEMI(); break;
                    case 4: viewLoan(); break;
                    case 5: listAllLoans(); break;
                    case 0: back = true; break;
                    default: System.out.println("⚠ Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }
    }

    private static void applyForLoan() throws BankingException {
        String customerId = getStringInput("Customer ID: ").toUpperCase();
        String accNo = getStringInput("Linked Account Number: ");

        System.out.println("\nLoan Types:");
        System.out.println("  1. Home Loan     (Rate: 8.5%, Max: 30 yrs)");
        System.out.println("  2. Personal Loan (Rate: 12.0%, Max: 5 yrs)");
        System.out.println("  3. Education Loan(Rate: 7.0%, Max: 15 yrs)");

        int typeChoice = getIntInput("Select Loan Type (1-3): ");
        LoanType loanType;
        switch (typeChoice) {
            case 1: loanType = LoanType.HOME_LOAN; break;
            case 2: loanType = LoanType.PERSONAL_LOAN; break;
            case 3: loanType = LoanType.EDUCATION_LOAN; break;
            default: throw new BankingException("Invalid loan type selection.");
        }

        double amount = getDoubleInput("Loan Amount: ₹");
        int tenure = getIntInput("Tenure (months): ");
        double income = getDoubleInput("Annual Income: ₹");

        Loan loan = loanService.applyForLoan(customerId, accNo, loanType, amount, tenure, income);
        loan.displayLoanDetails();
    }

    private static void approveLoan() throws BankingException {
        String loanId = getStringInput("Loan ID to approve: ").toUpperCase();
        loanService.approveLoan(loanId);
    }

    private static void payEMI() throws BankingException {
        String loanId = getStringInput("Loan ID: ").toUpperCase();
        loanService.payEMI(loanId);
    }

    private static void viewLoan() throws BankingException {
        String loanId = getStringInput("Loan ID: ").toUpperCase();
        Loan loan = loanService.findLoan(loanId);
        loan.displayLoanDetails();
    }

    private static void listAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        if (loans.isEmpty()) {
            System.out.println("ℹ No loan applications found.");
            return;
        }
        System.out.println("\n--- All Loans (" + loans.size() + ") ---");
        for (Loan loan : loans) {
            System.out.println("  " + loan);
        }
    }

    // ======================== BANK SUMMARY ========================

    private static void bankSummary() {
        BankServiceImpl serviceImpl = (BankServiceImpl) bankService;

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║               BANK SUMMARY                    ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  Total Customers  : %-28d ║%n", bankService.getAllCustomers().size());
        System.out.printf("║  Total Accounts   : %-28d ║%n", bankService.getAllAccounts().size());
        System.out.printf("║  Total Loans      : %-28d ║%n", loanService.getAllLoans().size());
        System.out.printf("║  Total Deposits   : ₹ %-26.2f ║%n", serviceImpl.getTotalDeposits());
        System.out.println("╠══════════════════════════════════════════════════╣");

        Map<String, Long> accountCounts = serviceImpl.getAccountCountByType();
        if (!accountCounts.isEmpty()) {
            System.out.println("║  ACCOUNTS BY TYPE:                               ║");
            for (Map.Entry<String, Long> entry : accountCounts.entrySet()) {
                System.out.printf("║    %-20s : %-24d ║%n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    // ======================== INPUT HELPERS ========================

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠ Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid number.");
            }
        }
    }
}
