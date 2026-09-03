// Account.java
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountId;
    private String pin;
    private double balance;
    private List<Transaction> history;

    public Account(String accountId, String pin, double balance) {
        this.accountId = accountId;
        this.pin = pin;
        this.balance = balance;
        this.history = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public boolean checkPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public List<Transaction> getHistory() {
        return history;
    }

    public void addTransaction(Transaction t) {
        history.add(t);
    }
}
