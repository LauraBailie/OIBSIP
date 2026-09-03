// ATM.java
import java.time.LocalDate;
import java.util.Scanner;

public class ATM {
    private Bank bank;
    private Scanner scanner;

    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Welcome to the ATM ===");

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        Account account = bank.getAccount(userId);

        if (account == null) {
            System.out.println("Account not found. Exiting.");
            return;
        }

        boolean loggedIn = false;
        int attempts = 0;

        while (attempts < 3) {
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();

            if (account.checkPin(pin)) {
                loggedIn = true;
                break;
            } else {
                attempts++;
                System.out.println("Incorrect PIN. Attempts left: " + (3 - attempts));
            }
        }

        if (!loggedIn) {
            System.out.println("Too many incorrect attempts. Access denied.");
            return;
        }

        System.out.println("Login successful!\n");
        showMenu(account);
    }

    private void showMenu(Account account) {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showHistory(account);
                    break;
                case "2":
                    withdraw(account);
                    break;
                case "3":
                    deposit(account);
                    break;
                case "4":
                    transfer(account);
                    break;
                case "5":
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void showHistory(Account account) {
        System.out.println("\n--- Transaction History ---");
        if (account.getHistory().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction t : account.getHistory()) {
            System.out.println(t.getDate() + " | " + t.getType() + " | $" + t.getAmount() + " | " + t.getDescription());
        }
    }

    private void withdraw(Account account) {
        System.out.print("Enter amount to withdraw: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (account.withdraw(amount)) {
            account.addTransaction(new Transaction("Withdraw", amount, today(), "Cash withdrawal"));
            System.out.println("Withdrawal successful. New balance: $" + account.getBalance());
        } else {
            System.out.println("Insufficient Funds.");
        }
    }

    private void deposit(Account account) {
        System.out.print("Enter amount to deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        account.deposit(amount);
        account.addTransaction(new Transaction("Deposit", amount, today(), "Cash deposit"));
        System.out.println("Deposit successful. New balance: $" + account.getBalance());
    }

    private void transfer(Account account) {
        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine();
        Account recipient = bank.getAccount(recipientId);

        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        if (recipient.getAccountId().equals(account.getAccountId())) {
            System.out.println("Cannot transfer to your own account.");
            return;
        }

        System.out.print("Enter amount to transfer: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (account.withdraw(amount)) {
            recipient.deposit(amount);
            account.addTransaction(new Transaction("Transfer Out", amount, today(), "Transfer to " + recipient.getAccountId()));
            recipient.addTransaction(new Transaction("Transfer In", amount, today(), "Transfer from " + account.getAccountId()));
            System.out.println("Transfer successful. New balance: $" + account.getBalance());
        } else {
            System.out.println("Insufficient Funds.");
        }
    }

    private String today() {
        return LocalDate.now().toString();
    }
}