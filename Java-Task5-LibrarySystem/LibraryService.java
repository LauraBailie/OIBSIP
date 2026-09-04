import java.util.*;

public class LibraryService {
    private final List<Book> books = new ArrayList<>();
    private final Map<String, Member> members = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();
    // bookId -> queue of usernames waiting for it (advance booking)
    private final Map<Integer, Queue<String>> reservationQueues = new HashMap<>();

    public LibraryService() {
        // Seed data so the app is testable immediately
        members.put("admin", new Member("admin", "admin123", "Library Admin", true));
        addBook("Clean Code", "Robert C. Martin", "9780132350884", "Software Engineering", 2);
        addBook("Effective Java", "Joshua Bloch", "9780134685991", "Software Engineering", 1);
        addBook("Introduction to Algorithms", "Cormen et al.", "9780262033848", "Computer Science", 3);
    }

    // ---------- Auth ----------
    public Member login(String username, String password) {
        Member m = members.get(username);
        return (m != null && m.checkPassword(password)) ? m : null;
    }

    public boolean register(String username, String password, String fullName) {
        if (members.containsKey(username)) return false;
        members.put(username, new Member(username, password, fullName, false));
        return true;
    }

    // ---------- Admin: catalogue ----------
    public Book addBook(String title, String author, String isbn, String category, int copies) {
        Book b = new Book(title, author, isbn, category, copies);
        books.add(b);
        return b;
    }

    public boolean deleteBook(int bookId) {
        return books.removeIf(b -> b.getId() == bookId);
    }

    public Optional<Book> findBook(int bookId) {
        return books.stream().filter(b -> b.getId() == bookId).findFirst();
    }

    public List<Book> allBooks() { return books; }

    public List<Book> search(String keyword) {
        String k = keyword.toLowerCase();
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(k) || b.getAuthor().toLowerCase().contains(k)
                    || b.getCategory().toLowerCase().contains(k)) {
                results.add(b);
            }
        }
        return results;
    }

    // ---------- User: issue / return / reserve ----------
    public String issueBook(String username, int bookId) {
        Optional<Book> maybeBook = findBook(bookId);
        if (maybeBook.isEmpty()) return "Book not found.";
        Book book = maybeBook.get();
        if (!book.isAvailable()) return "No copies available. Use Reserve to join the waiting list.";

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        loans.add(new Loan(bookId, username));
        return "Issued. Due back in 14 days.";
    }

    public String returnBook(int loanId) {
        Loan loan = loans.stream().filter(l -> l.getId() == loanId && !l.isReturned()).findFirst().orElse(null);
        if (loan == null) return "Active loan not found.";

        double fine = loan.markReturned();
        findBook(loan.getBookId()).ifPresent(b -> b.setAvailableCopies(b.getAvailableCopies() + 1));
        if (fine > 0) members.get(loan.getMemberUsername()).addFine(fine);

        // Notify next person in the reservation queue, if any
        Queue<String> queue = reservationQueues.get(loan.getBookId());
        String notified = (queue != null && !queue.isEmpty()) ? queue.poll() : null;

        String result = fine > 0 ? "Returned. Overdue fine: R" + fine : "Returned on time, no fine.";
        if (notified != null) result += " (Notify " + notified + ": their reserved copy is now available.)";
        return result;
    }

    public String reserveBook(String username, int bookId) {
        if (findBook(bookId).isEmpty()) return "Book not found.";
        reservationQueues.computeIfAbsent(bookId, k -> new LinkedList<>()).add(username);
        return "Reserved. You'll be next in line when a copy is returned.";
    }

    public List<Loan> loansForMember(String username) {
        return loans.stream().filter(l -> l.getMemberUsername().equals(username)).toList();
    }

    public List<Loan> activeLoans() {
        return loans.stream().filter(l -> !l.isReturned()).toList();
    }

    public boolean payFines(String username) {
        Member m = members.get(username);
        if (m == null) return false;
        m.clearFines();
        return true;
    }

    public Collection<Member> allMembers() { return members.values(); }
}
