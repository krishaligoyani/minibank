import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.*;

import model.Account;
import exception.InsufficientFundsException;

public class BankServiceTest {

    @Test
    public void testDepositIncreasesBalance() throws Exception {
        Repository<Account> repo = new Repository<>();
        BankService service = new BankService(repo);
        Account account = service.openAccount("Alice", 1000);

        service.deposit(account.getAccountNumber(), 500);

        assertEquals(1500, account.getBalance());
    }

    @Test(expected = InsufficientFundsException.class)
    public void testWithdrawOverLimitThrowsException() throws Exception {
        Repository<Account> repo = new Repository<>();
        BankService service = new BankService(repo);
        Account account = service.openAccount("Bob", 1000);

        service.withdraw(account.getAccountNumber(), 2500);
    }

    @Test
    public void testConcurrentDepositsKeepBalanceCorrect() throws Exception {
        Repository<Account> repo = new Repository<>();
        BankService service = new BankService(repo);
        Account account = service.openAccount("Charlie", 1000);

        TransactionProcessor processor = new TransactionProcessor();
        processor.addAccount(account);

        processor.processDepositBatch(account.getAccountNumber(), 100, 50, 4);

        assertEquals(6000, account.getBalance());
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(BankServiceTest.class);
        System.out.println("Tests run: " + result.getRunCount());
        System.out.println("Tests passed: " + result.wasSuccessful());
        System.out.println("Failures: " + result.getFailureCount());
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }
}
