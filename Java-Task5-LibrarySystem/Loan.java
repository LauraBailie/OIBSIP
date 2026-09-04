import java.time.LocalDate;

public class Loan {
    private static int nextId = 1;

    private final int id;
    private final int bookId;
    private final String memberUsername;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;

    public Loan(int bookId, String memberUsername) {
        this.id = nextId++;
        this.bookId = bookId;
        this.memberUsername = memberUsername;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusDays(14); // 14-day loan period
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getMemberUsername() { return memberUsername; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returnDate != null; }
    public double getFine() { return fine; }

    public double markReturned() {
        returnDate = LocalDate.now();
        if (returnDate.isAfter(dueDate)) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
            fine = daysLate * 5.0; // R5 per day late
        }
        return fine;
    }

    @Override
    public String toString() {
        String status = isReturned() ? "Returned " + returnDate : "Due " + dueDate;
        return String.format("Loan #%d | Book #%d | %s | %s%s",
                id, bookId, memberUsername, status, fine > 0 ? " | Fine: R" + fine : "");
    }
}
