# Digital Library Management System

Java Swing app for managing a book catalogue, issuing/returning books, and
tracking overdue fines.

## Demo
[Post On LinkedIn](https://www.linkedin.com/feed/update/urn:li:ugcPost:7501715474003619840/)

## Tech Stack
Java (core), Swing (GUI)

## Features
- Admin: add/delete books, view catalogue, active loans, and members
- User: register/login, search/browse books, issue a book, return a book
  (auto-calculates R5/day late fine), reserve a book that's currently out
- All data in-memory, seeded with a demo admin account and 3 books

## How to Run
```
javac *.java
java LibraryApp
```
Demo admin login: `admin` / `admin123`

## Files
- `LibraryApp.java` – Swing GUI (login, admin panel, user panel)
- `LibraryService.java` – all business logic (issuing, returns, fines, reservations)
- `Book.java`, `Member.java`, `Loan.java` – data models

## Author
Laura Bailie — OIBSIP Java Development Track
