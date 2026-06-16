package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles SQLite database connections and schema initialization.
 * Demonstrates: JDBC integration, SQL table creation.
 */
public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:banking.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void initializeDatabase() {
        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers ("
                + "customer_id TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "email TEXT NOT NULL, "
                + "phone TEXT NOT NULL, "
                + "pan TEXT NOT NULL"
                + ");";

        String createAccountsTable = "CREATE TABLE IF NOT EXISTS accounts ("
                + "account_number TEXT PRIMARY KEY, "
                + "customer_id TEXT, "
                + "account_type TEXT, "
                + "balance REAL, "
                + "FOREIGN KEY(customer_id) REFERENCES customers(customer_id)"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createCustomersTable);
            stmt.execute(createAccountsTable);
            System.out.println("✅ SQL Database initialized (SQLite).");
            
        } catch (SQLException e) {
            System.err.println("❌ Database initialization error: " + e.getMessage());
        }
    }

    // Example methods to insert data
    public void saveCustomer(String customerId, String name, String email, String phone, String pan) {
        String sql = "INSERT OR REPLACE INTO customers(customer_id, name, email, phone, pan) VALUES(?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, pan);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving customer to DB: " + e.getMessage());
        }
    }

    public void saveAccount(String accountNumber, String customerId, String accountType, double balance) {
        String sql = "INSERT OR REPLACE INTO accounts(account_number, customer_id, account_type, balance) VALUES(?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, customerId);
            pstmt.setString(3, accountType);
            pstmt.setDouble(4, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving account to DB: " + e.getMessage());
        }
    }

    public void deleteCustomer(String customerId) {
        String sql1 = "DELETE FROM accounts WHERE customer_id = ?";
        String sql2 = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(sql1);
             PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
            pstmt1.setString(1, customerId);
            pstmt1.executeUpdate();
            pstmt2.setString(1, customerId);
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting customer from DB: " + e.getMessage());
        }
    }
}
