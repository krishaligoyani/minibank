import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import model.Account;
import exception.BankException;
import exception.AccountNotFoundException;
import exception.InvalidAmountException;

public class MiniBank {

    private static final Scanner scanner = new Scanner(System.in);

    private static final Path DATA_FILE =
            Path.of("data", "accounts.dat");

    private static final Path LOG_FILE =
            Path.of("logs", "transactions.log");

    private static final Path REPORT_FILE =
            Path.of("reports", "daily-report.txt");

    private static final Path LOG_FOLDER =
            Path.of("logs");

    private static final Repository<Account> repository =
            new Repository<>();

    private static final BankService bankService =
            new BankService(repository, LOG_FILE);

    private static final TransactionProcessor processor =
            new TransactionProcessor();

    private static boolean running = true;

    public static void main(String[] args) {

        loadAccounts();

        while (running) {
            showMenu();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        openAccount();
                        break;

                    case "2":
                        depositMoney();
                        break;

                    case "3":
                        withdrawMoney();
                        break;

                    case "4":
                        transferMoney();
                        break;

                    case "5":
                        showStatement();
                        break;

                    case "6":
                        listAccounts();
                        break;

                    case "7":
                        generateReport();
                        break;

                    case "8":
                        batchDeposits();
                        break;

                    case "9":
                        saveAccounts();
                        running = false;
                        System.out.println("Data saved. Goodbye!");
                        break;

                    default:
                        System.out.println(
                                "Invalid choice. Please select 1-9."
                        );
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a valid number."
                );

            } catch (BankException e) {
                System.out.println(
                        "Transaction error: " + e.getMessage()
                );

            } catch (IllegalArgumentException e) {
                System.out.println(
                        "Input error: " + e.getMessage()
                );

            } catch (IOException e) {
                System.out.println(
                        "File error: " + e.getMessage()
                );
            }
        }

        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\n========== MINIBANK ==========");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Account Statement");
        System.out.println("6. List All Accounts");
        System.out.println("7. Generate Report");
        System.out.println("8. Batch Deposits");
        System.out.println("9. Save and Exit");
        System.out.println("==============================");
    }

    private static void openAccount() throws IOException {

        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter opening balance: Rs. ");
        long amount = Long.parseLong(scanner.nextLine().trim());

        Account account = bankService.openAccount(name, amount);

        processor.addAccount(account);

        System.out.println("Account created successfully!");
        System.out.println(account);
    }

    private static void depositMoney()
            throws BankException, IOException {

        System.out.print("Enter account number (e.g. AC0001): ");
        String accountNumber = scanner.nextLine().trim();

        System.out.print("Enter deposit amount: Rs. ");
        long amount = Long.parseLong(scanner.nextLine().trim());

        bankService.deposit(accountNumber, amount);

        System.out.println("Deposit successful!");
        System.out.println(bankService.statement(accountNumber));
    }

    private static void withdrawMoney()
            throws BankException, IOException {

        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine().trim();

        System.out.print("Enter withdrawal amount: Rs. ");
        long amount = Long.parseLong(scanner.nextLine().trim());

        bankService.withdraw(accountNumber, amount);

        System.out.println("Withdrawal successful!");
        System.out.println(bankService.statement(accountNumber));
    }

    private static void transferMoney()
            throws BankException, IOException {

        System.out.print("Enter sender account number: ");
        String from = scanner.nextLine().trim();

        System.out.print("Enter receiver account number: ");
        String to = scanner.nextLine().trim();

        System.out.print("Enter transfer amount: Rs. ");
        long amount = Long.parseLong(scanner.nextLine().trim());

        bankService.transfer(from, to, amount);

        System.out.println("Transfer successful!");
        System.out.println(bankService.statement(from));
        System.out.println(bankService.statement(to));
    }

    private static void showStatement() throws BankException {

        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine().trim();

        System.out.println(bankService.statement(accountNumber));
    }

    private static void listAccounts() {

        ArrayList<Account> accounts = bankService.listAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("\n------ ALL ACCOUNTS ------");

        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    private static void generateReport() throws IOException {

        Files.createDirectories(REPORT_FILE.getParent());

        ReportGenerator.generate(LOG_FOLDER, REPORT_FILE);

        System.out.println("Report generated at: " + REPORT_FILE);
        if (Files.exists(REPORT_FILE)) {
            System.out.println(Files.readString(REPORT_FILE));
        }
    }

    private static void batchDeposits() {

        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine().trim();

        System.out.print("Enter number of deposits: ");
        int count = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter amount per deposit: Rs. ");
        long amount = Long.parseLong(scanner.nextLine().trim());

        for (Account account : repository.findAll()) {
            processor.addAccount(account);
        }

        try {
            processor.processDepositBatch(
                    accountNumber,
                    count,
                    amount,
                    4
            );

            System.out.println("Batch deposits completed.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Batch processing interrupted.");

        } catch (InvalidAmountException e) {
            System.out.println(
                    "Invalid transaction: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Invalid batch input: " + e.getMessage()
            );
        }
    }

    private static void loadAccounts() {

        try {
            if (Files.exists(DATA_FILE)) {

                Account[] saved = StatePersister.load(DATA_FILE);

                repository.saveAll(saved);

                int maxNumber = 0;

                for (Account account : saved) {
                    processor.addAccount(account);

                    String number = account.getAccountNumber();

                    if (number != null && number.matches("AC\\d+")) {
                        int current = Integer.parseInt(
                                number.substring(2)
                        );

                        maxNumber = Math.max(maxNumber, current);
                    }
                }

                Account.updateCounter(maxNumber);

                System.out.println(
                        saved.length + " saved account(s) loaded."
                );

            } else {
                System.out.println(
                        "No previous account data found. Starting fresh."
                );
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "Could not load saved accounts: " + e.getMessage()
            );
        }
    }

    private static void saveAccounts() throws IOException {

        Files.createDirectories(DATA_FILE.getParent());

        ArrayList<Account> accounts = repository.findAll();

        StatePersister.save(accounts.toArray(new Account[0]), DATA_FILE);

        System.out.println(
                accounts.size() + " account(s) saved."
        );
    }
}
