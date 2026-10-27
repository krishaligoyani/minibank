package model;

import model.annotation.Id;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.io.Serializable;

public class Account implements Serializable, Comparable<Account> {

    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    private String ownerName;
    protected long balance;

    private static int counter = 0;

    public Account(String ownerName, long balance) {
        this.accountNumber = String.format("AC%04d", ++counter);
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public static synchronized void updateCounter(int maxNumber) {
        if (maxNumber > counter) {
            counter = maxNumber;
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public synchronized long getBalance() {
        return balance;
    }

    public double interestRate() {
        return 0.0;
    }

    public boolean canWithdraw(long amount) {
        return amount > 0 && balance >= amount;
    }

    public synchronized void deposit(long amount)
            throws InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                "Deposit amount must be greater than zero."
            );
        }

        balance += amount;
    }

    public synchronized void withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                "Withdrawal amount must be greater than zero."
            );
        }

        if (amount > balance) {
            throw new InsufficientFundsException(amount - balance);
        }

        balance -= amount;
    }

    public void transferSafelyTo(Account to, long amount)
            throws BankException {

        if (to == null) {
            throw new BankException(
                "Destination account not found."
            );
        }

        if (to == this) {
            throw new BankException(
                "Cannot transfer to the same account."
            );
        }

        Account first = this;
        Account second = to;

        if (first.getAccountNumber().compareTo(
                second.getAccountNumber()) > 0) {
            first = to;
            second = this;
        }

        synchronized (first) {
            synchronized (second) {
                withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(
            other.accountNumber
        );
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName
                + " | Balance: Rs. " + getBalance();
    }
}
