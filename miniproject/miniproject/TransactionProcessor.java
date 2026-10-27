import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.Account;
import exception.InvalidAmountException;

public class TransactionProcessor {

    private final ConcurrentHashMap<String, Account> accounts =
            new ConcurrentHashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void printAccounts() {
        for (Account account : accounts.values()) {
            System.out.println(account);
        }
    }

    public void processDepositBatch(
            String accountNumber,
            int totalDeposits,
            long amount,
            int workerCount)
            throws InvalidAmountException, InterruptedException {

        if (totalDeposits <= 0) {
            throw new IllegalArgumentException(
                    "Number of transactions must be positive."
            );
        }

        if (workerCount <= 0) {
            throw new IllegalArgumentException(
                    "Worker count must be positive."
            );
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero."
            );
        }

        Account account = accounts.get(accountNumber);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Account not found: " + accountNumber
            );
        }

        int workers = Math.min(workerCount, totalDeposits);
        ExecutorService executor =
                Executors.newFixedThreadPool(workers);

        List<Future<?>> tasks = new ArrayList<>();

        int base = totalDeposits / workers;
        int remainder = totalDeposits % workers;

        try {
            for (int i = 0; i < workers; i++) {
                int depositsForWorker =
                        base + (i < remainder ? 1 : 0);

                tasks.add(executor.submit(
                        new AccountWorker(
                                account,
                                depositsForWorker,
                                amount
                        )
                ));
            }

            for (Future<?> task : tasks) {
                try {
                    task.get();
                } catch (java.util.concurrent.ExecutionException e) {
                    throw new IllegalStateException(
                            "A batch worker failed.", e.getCause()
                    );
                }
            }
        } finally {
            executor.shutdown();
        }

        System.out.println(
                "Batch completed: " + totalDeposits
                + " deposit attempts."
        );
        System.out.println(account);
    }
}
