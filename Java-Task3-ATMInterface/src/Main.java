// Main.java
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();

        // Sample accounts for testing
        bank.addAccount(new Account("1001", "1234", 500.00));
        bank.addAccount(new Account("1002", "5678", 1000.00));

        ATM atm = new ATM(bank);
        atm.start();
    }
}
