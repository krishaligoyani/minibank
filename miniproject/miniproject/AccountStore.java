
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import model.Account;

public class AccountStore {

    private final HashMap<String, Account> accounts =
            new HashMap<>();

    private final ArrayList<String> transactionHistory =
            new ArrayList<>();

    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted = getAll();

        sorted.sort(
            Comparator.comparingLong(Account::getBalance)
        );

        return sorted;
    }
}
