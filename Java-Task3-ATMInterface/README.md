# ATM Interface

A console-based ATM simulation built in Java as part of the Oasis Infobyte Java Development internship (Task 3).

## Objective

Simulate the core functions of an ATM: user authentication, withdrawals, deposits, transfers, and transaction history — all handled through a simple menu-driven console app.

## Features

- Login with User ID + PIN, access denied after 3 incorrect attempts
- Main menu with 5 options: Transaction History, Withdraw, Deposit, Transfer, Quit
- Balance check before any withdrawal or transfer ("Insufficient Funds" if balance is too low)
- All transactions logged to an `ArrayList` and viewable in Transaction History
- Transfers update both the sender's and recipient's accounts and log on both sides

## Tech Stack

- Java (console application)
- Object-Oriented design — no external libraries or database

## Classes

| Class | Responsibility |
|---|---|
| `Main` | Entry point; sets up sample accounts and starts the ATM |
| `Bank` | Stores all accounts, looks them up by ID |
| `Account` | Holds balance, PIN, and transaction history for one account |
| `Transaction` | Simple data record for a single transaction (type, amount, date, description) |
| `ATM` | Handles login flow and the menu-driven transaction logic |

## How to Run

```bash
javac *.java
java Main
```

Two sample accounts are seeded in `Main.java` for testing:

| User ID | PIN | Starting Balance |
|---|---|---|
| 1001 | 1234 | $500.00 |
| 1002 | 5678 | $1000.00 |

## Known Limitations / Future Improvements

- No persistence — balances and history reset every run. Next step would be connecting to a database via JDBC (SQLite/MySQL) so data survives between sessions.
- No unit tests yet around the balance-validation logic.
- PIN and balances are stored in plain memory (fine for a learning project, not production-ready).

## Author

Built as part of the Oasis Infobyte Summer Internship Program (SIP), Java Development track.