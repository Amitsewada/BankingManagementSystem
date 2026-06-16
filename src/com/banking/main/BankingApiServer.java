package com.banking.main;

import com.banking.model.Customer;
import com.banking.model.Account;
import com.banking.model.Loan;
import com.banking.model.Transaction;
import com.banking.model.enums.LoanType;
import com.banking.service.BankServiceImpl;
import com.banking.service.LoanServiceImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * A lightweight HTTP Server to serve the Web UI and handle REST API requests.
 * Demonstrates: HTTP Server, REST API design, JSON serialization
 */
public class BankingApiServer {

    public static void startServer() throws IOException {
        int port = 8090;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new StaticFileHandler());

        // ==================== Status API ====================
        server.createContext("/api/status", exchange -> {
            sendResponse(exchange, 200, "{\"status\": \"online\"}");
        });

        // ==================== Summary Dashboard API ====================
        server.createContext("/api/summary", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                BankServiceImpl svc = (BankServiceImpl) BankingApplication.bankService;
                long customerCount = svc.getAllCustomers().size();
                long accountCount = svc.getAllAccounts().size();
                double totalDeposits = svc.getTotalDeposits();
                long loanCount = BankingApplication.loanService.getAllLoans().size();
                long activeLoans = BankingApplication.loanService.getAllLoans().stream()
                        .filter(Loan::isActive).count();
                double totalLoanAmount = BankingApplication.loanService.getAllLoans().stream()
                        .mapToDouble(Loan::getPrincipalAmount).sum();

                String json = String.format(
                    "{\"customers\":%d, \"accounts\":%d, \"deposits\":%.2f, \"totalLoans\":%d, \"activeLoans\":%d, \"totalLoanAmount\":%.2f}",
                    customerCount, accountCount, totalDeposits, loanCount, activeLoans, totalLoanAmount);
                sendResponse(exchange, 200, json);
            }
        });

        // ==================== Mini Statement API ====================
        server.createContext("/api/transactions/statement", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String accNo = query.split("=")[1];
                    List<Transaction> txns = BankingApplication.bankService.getMiniStatement(accNo, 10);

                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < txns.size(); i++) {
                        Transaction t = txns.get(i);
                        json.append(String.format(
                            "{\"id\":\"%s\", \"type\":\"%s\", \"amount\":%.2f, \"balance\":%.2f, \"desc\":\"%s\"}",
                            t.getTransactionId(), t.getType(), t.getAmount(),
                            t.getBalanceAfterTransaction(), t.getDescription()));
                        if (i < txns.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Customers API ====================
        server.createContext("/api/customers", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Customer> customers = BankingApplication.bankService.getAllCustomers();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < customers.size(); i++) {
                    Customer c = customers.get(i);
                    json.append(String.format(
                        "{\"id\":\"%s\", \"name\":\"%s\", \"email\":\"%s\", \"phone\":\"%s\", \"pan\":\"%s\", \"balance\":%.2f, \"accountCount\":%d}",
                        c.getCustomerId(), escapeJson(c.getName()), escapeJson(c.getEmail()),
                        c.getPhone(), c.getPanNumber(), c.getTotalBalance(), c.getAccounts().size()));
                    if (i < customers.size() - 1) json.append(",");
                }
                json.append("]");
                sendResponse(exchange, 200, json.toString());
            } else if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    Customer c = BankingApplication.bankService.registerCustomer(
                        params.get("name"), params.get("email"), params.get("phone"), params.get("pan")
                    );
                    sendResponse(exchange, 200, "{\"success\":true, \"customerId\":\"" + c.getCustomerId() + "\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Delete Customer API ====================
        server.createContext("/api/customers/delete", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String custId = params.get("customerId");
                    BankServiceImpl svc = (BankServiceImpl) BankingApplication.bankService;

                    // Also check for active loans
                    List<Loan> custLoans = BankingApplication.loanService.getCustomerLoans(custId);
                    for (Loan l : custLoans) {
                        if (l.isActive()) {
                            sendResponse(exchange, 400,
                                "{\"error\":\"Cannot delete customer. Loan " + l.getLoanId()
                                + " is still active (Remaining: ₹" + String.format("%.2f", l.getRemainingAmount())
                                + "). Please settle all loans first.\"}");
                            return;
                        }
                    }

                    svc.deleteCustomer(custId);
                    sendResponse(exchange, 200, "{\"success\":true}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Accounts API ====================
        server.createContext("/api/accounts", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Account> accounts = BankingApplication.bankService.getAllAccounts();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < accounts.size(); i++) {
                    Account a = accounts.get(i);
                    json.append(String.format(
                        "{\"accNo\":\"%s\", \"holder\":\"%s\", \"type\":\"%s\", \"balance\":%.2f, \"active\":%b}",
                        a.getAccountNumber(), escapeJson(a.getHolderName()),
                        a.getAccountType().getDisplayName(), a.getBalance(), a.isActive()));
                    if (i < accounts.size() - 1) json.append(",");
                }
                json.append("]");
                sendResponse(exchange, 200, json.toString());
            } else if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String customerId = params.get("customerId");
                    double deposit = Double.parseDouble(params.get("deposit"));
                    String type = params.get("type");

                    Account a;
                    if ("SAVINGS".equals(type)) {
                        a = BankingApplication.bankService.openSavingsAccount(customerId, deposit);
                    } else if ("CURRENT".equals(type)) {
                        a = BankingApplication.bankService.openCurrentAccount(customerId, deposit, 50000.0);
                    } else {
                        a = BankingApplication.bankService.openFixedDepositAccount(customerId, deposit, 12);
                    }
                    sendResponse(exchange, 200,
                        "{\"success\":true, \"accountNumber\":\"" + a.getAccountNumber() + "\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Close Account API ====================
        server.createContext("/api/accounts/close", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String accNo = params.get("accountNumber");
                    BankingApplication.bankService.closeAccount(accNo);
                    sendResponse(exchange, 200, "{\"success\":true}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Interest API ====================
        server.createContext("/api/accounts/interest", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String accNo = params.get("accountNumber");
                    Account acc = BankingApplication.bankService.findAccount(accNo);
                    double interest = acc.calculateInterest();
                    BankingApplication.bankService.calculateAndCreditInterest(accNo);
                    sendResponse(exchange, 200,
                        "{\"success\":true, \"interest\":" + String.format("%.2f", interest)
                        + ", \"newBalance\":" + String.format("%.2f", acc.getBalance()) + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Transaction APIs ====================
        server.createContext("/api/transactions/deposit", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String accNo = params.get("accountNumber");
                    double amount = Double.parseDouble(params.get("amount"));
                    BankingApplication.bankService.deposit(accNo, amount);
                    Account acc = BankingApplication.bankService.findAccount(accNo);
                    sendResponse(exchange, 200,
                        "{\"success\":true, \"newBalance\":" + String.format("%.2f", acc.getBalance()) + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        server.createContext("/api/transactions/withdraw", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String accNo = params.get("accountNumber");
                    double amount = Double.parseDouble(params.get("amount"));
                    BankingApplication.bankService.withdraw(accNo, amount);
                    Account acc = BankingApplication.bankService.findAccount(accNo);
                    sendResponse(exchange, 200,
                        "{\"success\":true, \"newBalance\":" + String.format("%.2f", acc.getBalance()) + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        server.createContext("/api/transactions/transfer", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String fromAcc = params.get("fromAccount");
                    String toAcc = params.get("toAccount");
                    double amount = Double.parseDouble(params.get("amount"));
                    BankingApplication.bankService.transfer(fromAcc, toAcc, amount);
                    sendResponse(exchange, 200, "{\"success\":true}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Loan APIs ====================
        server.createContext("/api/loans", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Loan> loans = BankingApplication.loanService.getAllLoans();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < loans.size(); i++) {
                    Loan l = loans.get(i);
                    String status = l.isActive() ? "Active" : (l.isApproved() ? "Closed" : "Pending");
                    json.append(String.format(
                        "{\"id\":\"%s\", \"custId\":\"%s\", \"type\":\"%s\", \"principal\":%.2f, \"emi\":%.2f, \"totalPayable\":%.2f, \"amountPaid\":%.2f, \"remaining\":%.2f, \"emisPaid\":%d, \"totalEmis\":%d, \"status\":\"%s\"}",
                        l.getLoanId(), l.getCustomerId(), l.getLoanType().getDisplayName(),
                        l.getPrincipalAmount(), l.getEmiAmount(), l.getTotalAmountPayable(),
                        l.getAmountPaid(), l.getRemainingAmount(), l.getEmisPaid(),
                        l.getTenureMonths(), status));
                    if (i < loans.size() - 1) json.append(",");
                }
                json.append("]");
                sendResponse(exchange, 200, json.toString());
            } else if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String customerId = params.get("customerId");
                    String accNo = params.get("accountNumber");
                    String typeStr = params.get("type");
                    double amount = Double.parseDouble(params.get("amount"));
                    int tenure = Integer.parseInt(params.get("tenure"));
                    double income = Double.parseDouble(params.get("income"));

                    LoanType type = LoanType.valueOf(typeStr);
                    Loan loan = BankingApplication.loanService.applyForLoan(
                        customerId, accNo, type, amount, tenure, income);
                    loan.approveLoan();
                    sendResponse(exchange, 200,
                        "{\"success\":true, \"loanId\":\"" + loan.getLoanId()
                        + "\", \"emi\":" + String.format("%.2f", loan.getEmiAmount())
                        + ", \"totalPayable\":" + String.format("%.2f", loan.getTotalAmountPayable()) + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Settle Loan API ====================
        server.createContext("/api/loans/settle", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String loanId = params.get("loanId");
                    LoanServiceImpl loanSvc = (LoanServiceImpl) BankingApplication.loanService;
                    Loan loan = loanSvc.findLoan(loanId);
                    double remaining = loan.getRemainingAmount();
                    int remainingEMIs = loan.getRemainingEMIs();
                    String linkedAcc = loan.getLinkedAccountNumber();

                    // Debit the settlement amount from linked account
                    BankingApplication.bankService.withdraw(linkedAcc, remaining);

                    loanSvc.settleLoanFull(loanId);
                    Account acc = BankingApplication.bankService.findAccount(linkedAcc);
                    sendResponse(exchange, 200,
                        "{\"success\":true, \"settledAmount\":" + String.format("%.2f", remaining)
                        + ", \"emisCleared\":" + remainingEMIs
                        + ", \"newBalance\":" + String.format("%.2f", acc.getBalance()) + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        // ==================== Pay Single EMI API ====================
        server.createContext("/api/loans/payemi", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = parseFormData(exchange.getRequestBody());
                    String loanId = params.get("loanId");
                    Loan loan = BankingApplication.loanService.findLoan(loanId);
                    String linkedAcc = loan.getLinkedAccountNumber();
                    double emiAmt = loan.getEmiAmount();

                    // Debit EMI from linked account
                    BankingApplication.bankService.withdraw(linkedAcc, emiAmt);

                    BankingApplication.loanService.payEMI(loanId);
                    Account acc = BankingApplication.bankService.findAccount(linkedAcc);
                    sendResponse(exchange, 200,
                        "{\"success\":true, \"remainingEMIs\":" + loan.getRemainingEMIs()
                        + ", \"amountPaid\":" + String.format("%.2f", loan.getAmountPaid())
                        + ", \"newBalance\":" + String.format("%.2f", acc.getBalance())
                        + ", \"active\":" + loan.isActive() + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("🌐 Web UI Server started on http://localhost:" + port);
    }

    // ==================== Helper Methods ====================

    private static String escapeJson(String text) {
        if (text == null) return "Unknown error";
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static Map<String, String> parseFormData(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String body = reader.readLine();
        Map<String, String> params = new HashMap<>();
        if (body != null) {
            for (String pair : body.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length > 1) {
                    params.put(kv[0], java.net.URLDecoder.decode(kv[1], "UTF-8"));
                }
            }
        }
        return params;
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }
            File file = new File("web" + path);
            if (file.exists() && !file.isDirectory()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                if (path.endsWith(".html")) exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                if (path.endsWith(".css")) exchange.getResponseHeaders().set("Content-Type", "text/css");
                if (path.endsWith(".js")) exchange.getResponseHeaders().set("Content-Type", "application/javascript");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
