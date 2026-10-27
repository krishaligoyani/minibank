import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import model.Account;
import exception.BankException;
import exception.AccountNotFoundException;
import annotation.Audit;

public class BankService {

    private final Repository<Account> repository;
    private final Path logFile;

    public BankService(Repository<Account> repository) {
        this(repository, null);
    }

    public BankService(Repository<Account> repository, Path logFile) {
        this.repository = repository;
        this.logFile = logFile;
    }

    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(String name, long openingBalance) {
        name = Validator.validateName(name);
        Validator.validateAmount(openingBalance);

        Account account = new Account(name, openingBalance);
        repository.save(account);

        AuditLogger.log(BankService.class, "openAccount");
        return account;
    }

    public Account findAccount(String accountNumber)
            throws AccountNotFoundException {

        Account account = repository.findById(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(
                "Account not found: " + accountNumber
            );
        }

        return account;
    }

    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, long amount)
            throws BankException, IOException {

        Validator.validateAmount(amount);
        Account account = findAccount(accountNumber);

        account.deposit(amount);

        if (logFile != null) {
            TransactionLog.append(
                logFile, "DEPOSIT," + amount
            );
        }

        AuditLogger.log(BankService.class, "deposit");
    }

    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, long amount)
            throws BankException, IOException {

        Validator.validateAmount(amount);
        Account account = findAccount(accountNumber);

        account.withdraw(amount);

        if (logFile != null) {
            TransactionLog.append(
                logFile, "WITHDRAW," + amount
            );
        }

        AuditLogger.log(BankService.class, "withdraw");
    }

    @Audit(action = "TRANSFER")
    public void transfer(
            String fromAccountNumber,
            String toAccountNumber,
            long amount) throws BankException, IOException {

        Validator.validateAmount(amount);

        Account from = findAccount(fromAccountNumber);
        Account to = findAccount(toAccountNumber);

        from.transferSafelyTo(to, amount);

        if (logFile != null) {
            TransactionLog.append(
                logFile, "TRANSFER," + amount
            );
        }

        AuditLogger.log(BankService.class, "transfer");
    }

    @Audit(action = "STATEMENT")
    public Account statement(String accountNumber)
            throws AccountNotFoundException {

        Account account = findAccount(accountNumber);
        AuditLogger.log(BankService.class, "statement");
        return account;
    }

    @Audit(action = "LIST_ACCOUNTS")
    public ArrayList<Account> listAccounts() {
        AuditLogger.log(BankService.class, "listAccounts");
        return repository.findAll();
    }
}
