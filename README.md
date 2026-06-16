# 🏦 LastBank — Banking Management System

A comprehensive, full-stack Banking Management System built with **Core Java**, demonstrating advanced **Object-Oriented Programming** concepts, **Design Patterns**, **SQLite JDBC** persistence, and a modern **Web UI** — all without any heavy frameworks.


---

## 🚀 Features

### Core Banking Operations
- **Customer Management** — Register, view, search, and delete customers with KYC validation (PAN, Phone, Email)
- **Multi-Type Accounts** — Savings, Current, and Fixed Deposit accounts with polymorphic interest logic
- **Real-time Transactions** — Deposit, Withdraw, Transfer with ACID-like validation and transaction logging
- **Mini Statements** — View last 10 transactions with color-coded credit/debit indicators

### Loan Processing Engine
- **3 Loan Types** — Home Loan (8.5%), Personal Loan (12%), Education Loan (7%)
- **Automated Eligibility** — Income-based LTV ratio validation before approval
- **EMI Calculator** — Standard EMI formula with monthly interest compounding
- **Pay Single EMI** — Pay one installment at a time, debited from linked bank account
- **One-Click Settlement** — Settle entire loan in one payment with full balance deduction
- **Loan Portfolio Dashboard** — Track paid/remaining EMIs, status, and outstanding amounts

### Advanced Features
- **Interest Calculator** — Calculate and credit interest to Savings & FD accounts
- **Account Closure** — Close accounts with status tracking
- **Customer Deletion** — Delete customers only after all loans settled & accounts closed (business rule enforcement)
- **SQLite Persistence** — All customer and account data persisted via JDBC
- **Transaction Logging** — File-based audit trail for all operations

### Web UI Dashboard
- **6 Live Stat Cards** — Total Deposits, Customers, Accounts, Total Loans, Active Loans, Loan Disbursed
- **Animated Number Counters** — Values count up on page load
- **Dynamic Info Boxes** — Transaction rules and loan eligibility criteria update in real-time
- **Toast Notifications** — Success/Error feedback with slide-in animations
- **Color-coded Tables** — Green for credits, Red for debits, Status badges
- **Navigation Tabs** — Dashboard, Customers, Accounts, Loans views

---

## 🏗️ Architecture & Design Patterns

```
┌─────────────────────────────────────────────────────────────┐
│                      Web UI (HTML/CSS/JS)                   │
│              Animated Dashboard + REST Client               │
├─────────────────────────────────────────────────────────────┤
│                 BankingApiServer (REST API)                  │
│              com.sun.net.httpserver on :8090                 │
├──────────────────────┬──────────────────────────────────────┤
│    BankService       │       LoanService                    │
│  (Interface)         │      (Interface)                     │
├──────────────────────┼──────────────────────────────────────┤
│  BankServiceImpl     │     LoanServiceImpl                  │
│  (Implementation)    │    (Implementation)                  │
├──────────────────────┴──────────────────────────────────────┤
│                    Model Layer                               │
│  Account (Abstract) → SavingsAccount, CurrentAccount, FD    │
│  Loan (Abstract) → HomeLoan, PersonalLoan, EducationLoan    │
│  Customer, Transaction (Immutable)                          │
├─────────────────────────────────────────────────────────────┤
│              DatabaseManager (SQLite JDBC)                   │
│              IdGenerator | TransactionLogger                 │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Singleton** | `IdGenerator`, `DatabaseManager`, `TransactionLogger` | Single instance for ID generation, DB access, logging |
| **Factory Method** | `LoanServiceImpl.applyForLoan()` | Creates `HomeLoan`, `PersonalLoan`, or `EducationLoan` based on type |
| **Template Method** | `Account.displayAccountDetails()` | Base display + polymorphic `displayAccountSpecificDetails()` |
| **Strategy** (via Polymorphism) | `Account.calculateInterest()` | Each account type has its own interest calculation |

### OOP Concepts Demonstrated

- **Abstraction** — `Account` and `Loan` abstract classes, `BankService` and `LoanService` interfaces
- **Encapsulation** — Private fields with controlled getters/setters, defensive copies
- **Inheritance** — `SavingsAccount extends Account`, `HomeLoan extends Loan`, etc.
- **Polymorphism** — `account.calculateInterest()` behaves differently per type
- **Composition** — `Customer` contains `List<Account>`
- **Immutability** — `Transaction` class is fully immutable

---

## 📁 Project Structure

```
BankingManagementSystem/
├── src/com/banking/
│   ├── main/
│   │   ├── BankingApplication.java      # Entry point + Console UI
│   │   └── BankingApiServer.java        # REST API server (:8090)
│   ├── model/
│   │   ├── Account.java                 # Abstract base class
│   │   ├── SavingsAccount.java          # 4% interest, 5 withdrawals/day
│   │   ├── CurrentAccount.java          # Overdraft support
│   │   ├── FixedDepositAccount.java     # Compound interest, lock-in
│   │   ├── Customer.java                # Customer with account list
│   │   ├── Transaction.java             # Immutable transaction record
│   │   ├── Loan.java                    # Abstract loan base
│   │   ├── HomeLoan.java                # 8.5%, property-backed
│   │   ├── PersonalLoan.java            # 12%, unsecured
│   │   ├── EducationLoan.java           # 7%, with moratorium
│   │   └── enums/                       # AccountType, LoanType, TransactionType
│   ├── service/
│   │   ├── BankService.java             # Interface
│   │   ├── BankServiceImpl.java         # Full implementation
│   │   ├── LoanService.java             # Interface
│   │   └── LoanServiceImpl.java         # Full implementation
│   ├── exception/                       # Custom exceptions (5 types)
│   └── util/
│       ├── DatabaseManager.java         # SQLite JDBC singleton
│       ├── IdGenerator.java             # Sequential ID generator
│       └── TransactionLogger.java       # File-based audit logger
├── web/
│   ├── index.html                       # Web UI dashboard
│   └── style.css                        # Premium CSS with animations
├── lib/
│   └── sqlite-jdbc-3.47.2.0.jar         # SQLite JDBC driver
├── run.bat                              # One-click compile & run
```



The application starts:
1. **Console Interface** — Interactive menu-driven CLI
2. **Web UI** — Opens at [http://localhost:8090](http://localhost:8090)

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 (Core Java, no frameworks) |
| **Database** | SQLite via JDBC |
| **Web Server** | `com.sun.net.httpserver` (built-in JDK) |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript |
| **Build** | Manual compilation via `javac` (no Maven/Gradle needed) |

---

## 📊 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/status` | Server health check |
| `GET` | `/api/summary` | Dashboard statistics |
| `GET/POST` | `/api/customers` | List / Register customers |
| `POST` | `/api/customers/delete` | Delete customer (with validation) |
| `GET/POST` | `/api/accounts` | List / Open accounts |
| `POST` | `/api/accounts/close` | Close an account |
| `POST` | `/api/accounts/interest` | Calculate & credit interest |
| `POST` | `/api/transactions/deposit` | Deposit funds |
| `POST` | `/api/transactions/withdraw` | Withdraw funds |
| `POST` | `/api/transactions/transfer` | Transfer between accounts |
| `GET` | `/api/transactions/statement` | Mini statement (last 10 txns) |
| `GET/POST` | `/api/loans` | List / Apply for loans |
| `POST` | `/api/loans/payemi` | Pay single EMI |
| `POST` | `/api/loans/settle` | One-time full loan settlement |

---

## 🔮 Future Roadmap

- [ ] Migrate to **Spring Boot** with dependency injection
- [ ] Add **JWT Authentication** for API security

