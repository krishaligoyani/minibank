# MiniBank — Final Capstone Project (Milestone 3)

MiniBank is a core banking simulation application written in Java. It brings together generic repositories, reflection and custom annotations, multithreaded transaction processing, file-based logging and reporting, object serialization, and JUnit testing.

---

## Features

1. **BankService**
   - Central business service coordinating account operations.
   - Operations: `openAccount`, `deposit`, `withdraw`, `transfer`, `statement`, `listAccounts`.
   - Comprehensive input validation (`Validator`) and domain exceptions (`BankException`, `InsufficientFundsException`, `InvalidAmountException`, `AccountNotFoundException`).

2. **Persistence (StatePersister)**
   - Automatically loads saved accounts from `data/accounts.dat` on startup.
   - Automatically saves account states to `data/accounts.dat` on exit.
   - Accurately tracks account counter state across restarts.

3. **Multithreaded Batch Processing (TransactionProcessor)**
   - Concurrent batch deposit processing using a fixed thread pool (`ExecutorService`).
   - Ensures thread-safe synchronization over accounts to prevent race conditions.

4. **Reporting & Logging (ReportGenerator & TransactionLog)**
   - Logs all transactions (deposits, withdrawals, transfers) to `logs/transactions.log`.
   - Generates formatted summary reports in `reports/daily-report.txt` tracking deposit and withdrawal totals.

5. **Auditing with Reflection (@Audit)**
   - Custom `@Audit` runtime annotation applied to service methods.
   - `AuditLogger` reflects over method metadata to dynamically log invoked operations.

6. **Unit Testing**
   - JUnit tests in `BankServiceTest` verifying:
     - Deposits correctly increment balance.
     - Withdrawals exceeding balance throw `InsufficientFundsException`.
     - Concurrent deposits maintain exact balance integrity under multithreaded load.

---

## Project Structure

```
miniproject/
├── annotation/
│   └── Audit.java
├── data/
│   └── accounts.dat
├── exception/
│   ├── AccountNotFoundException.java
│   ├── BankException.java
│   ├── InsufficientFundsException.java
│   └── InvalidAmountException.java
├── lib/
│   ├── hamcrest-core-1.3.jar
│   └── junit-4.13.2.jar
├── logs/
│   └── transactions.log
├── model/
│   ├── annotation/
│   │   └── Id.java
│   ├── Account.java
│   ├── BankUtil.java
│   ├── CurrentAccount.java
│   ├── FixedDepositAccount.java
│   ├── InterestBearing.java
│   ├── Premium.java
│   ├── SavingsAccount.java
│   ├── Transactable.java
│   └── WithdrawRule.java
├── out/
├── reports/
│   └── daily-report.txt
├── AccountStore.java
├── AccountWorker.java
├── AuditLogger.java
├── BankService.java
├── BankServiceTest.java
├── manifest.txt
├── MiniBank.jar
├── MiniBank.java
├── README.md
├── ReportGenerator.java
├── Repository.java
├── StatePersister.java
├── TransactionBuffer.java
├── TransactionLog.java
├── TransactionProcessor.java
└── Validator.java
```

---

## How to Compile, Test, and Run

### 1. Compile the Project

From the project root directory:

```powershell
javac -cp ".;lib/*" -d out (Get-ChildItem -Path . -Filter *.java -Recurse | ForEach-Object { $_.FullName })
```

### 2. Run the Unit Tests

Execute test suite via JUnit:

```powershell
java -cp "out;lib/*" org.junit.runner.JUnitCore BankServiceTest
```

Alternatively:

```powershell
java -cp "out;lib/*" BankServiceTest
```

### 3. Build Runnable JAR

Package all compiled classes into `MiniBank.jar`:

```powershell
jar cfm MiniBank.jar manifest.txt -C out .
```

### 4. Run the Application

```powershell
java -jar MiniBank.jar
```

---

## Interactive Menu Options

1. **Open Account**: Create a new account with holder name and opening balance.
2. **Deposit Money**: Deposit funds into an existing account.
3. **Withdraw Money**: Withdraw funds subject to available balance.
4. **Transfer Money**: Deadlock-free funds transfer between two accounts.
5. **Account Statement**: Display current details and balance for an account.
6. **List All Accounts**: View all active accounts in the repository.
7. **Generate Report**: Read transaction logs and write daily summary to file.
8. **Batch Deposits**: Concurrently deposit funds using worker threads.
9. **Save and Exit**: Serialize all accounts to disk and terminate.
