import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LibraryApp extends JFrame {

    private final LibraryService service = new LibraryService();
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);
    private Member currentUser;

    private JTextArea catalogueArea, myLoansArea, adminIssuedArea;

    public LibraryApp() {
        setTitle("Digital Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        cards.add(buildLoginPanel(), "login");
        cards.add(buildUserPanel(), "user");
        cards.add(buildAdminPanel(), "admin");
        add(cards);
    }

    // ---------- Login / Register ----------
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JTextField nameField = new JTextField(15);
        JLabel status = new JLabel(" ");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Full name (register only):"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.addActionListener(e -> {
            Member m = service.login(userField.getText(), new String(passField.getPassword()));
            if (m == null) {
                status.setText("Invalid username or password.");
            } else {
                currentUser = m;
                if (m.isAdmin()) {
                    refreshAdminView();
                    cardLayout.show(cards, "admin");
                } else {
                    refreshUserView();
                    cardLayout.show(cards, "user");
                }
            }
        });

        registerBtn.addActionListener(e -> {
            boolean ok = service.register(userField.getText(), new String(passField.getPassword()), nameField.getText());
            status.setText(ok ? "Registered! Now click Login." : "That username is already taken.");
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(loginBtn, gbc);
        gbc.gridy = 4; panel.add(registerBtn, gbc);
        gbc.gridy = 5; panel.add(status, gbc);
        gbc.gridy = 6; panel.add(new JLabel("Demo admin login: admin / admin123"), gbc);
        return panel;
    }

    // ---------- User module ----------
    private JPanel buildUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        JButton browseBtn = new JButton("Show All");
        catalogueArea = new JTextArea(10, 40);
        catalogueArea.setEditable(false);

        searchBtn.addActionListener(e -> showBooks(service.search(searchField.getText())));
        browseBtn.addActionListener(e -> showBooks(service.allBooks()));

        JPanel searchRow = new JPanel(new BorderLayout(5, 5));
        searchRow.add(searchField, BorderLayout.CENTER);
        JPanel searchButtons = new JPanel();
        searchButtons.add(searchBtn);
        searchButtons.add(browseBtn);
        searchRow.add(searchButtons, BorderLayout.EAST);

        JTextField bookIdField = new JTextField(5);
        JButton issueBtn = new JButton("Issue by ID");
        JButton reserveBtn = new JButton("Reserve by ID");
        JLabel actionStatus = new JLabel(" ");

        issueBtn.addActionListener(e -> {
            actionStatus.setText(withId(bookIdField, id -> service.issueBook(currentUser.getUsername(), id)));
            refreshUserView();
        });
        reserveBtn.addActionListener(e -> {
            actionStatus.setText(withId(bookIdField, id -> service.reserveBook(currentUser.getUsername(), id)));
        });

        JPanel actionRow = new JPanel();
        actionRow.add(new JLabel("Book ID:"));
        actionRow.add(bookIdField);
        actionRow.add(issueBtn);
        actionRow.add(reserveBtn);

        myLoansArea = new JTextArea(6, 40);
        myLoansArea.setEditable(false);
        JTextField loanIdField = new JTextField(5);
        JButton returnBtn = new JButton("Return by Loan ID");
        returnBtn.addActionListener(e -> {
            actionStatus.setText(withId(loanIdField, id -> service.returnBook(id)));
            refreshUserView();
        });
        JPanel returnRow = new JPanel();
        returnRow.add(new JLabel("Loan ID:"));
        returnRow.add(loanIdField);
        returnRow.add(returnBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> { currentUser = null; cardLayout.show(cards, "login"); });

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(searchRow, BorderLayout.NORTH);
        top.add(new JScrollPane(catalogueArea), BorderLayout.CENTER);
        top.add(actionRow, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.add(new JLabel("My Loans:"), BorderLayout.NORTH);
        bottom.add(new JScrollPane(myLoansArea), BorderLayout.CENTER);
        bottom.add(returnRow, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 5, 5));
        center.add(top);
        center.add(bottom);

        panel.add(center, BorderLayout.CENTER);
        panel.add(actionStatus, BorderLayout.NORTH);
        panel.add(logoutBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void showBooks(List<Book> list) {
        StringBuilder sb = new StringBuilder();
        for (Book b : list) sb.append(b).append("\n");
        catalogueArea.setText(sb.length() == 0 ? "No results." : sb.toString());
    }

    private void refreshUserView() {
        showBooks(service.allBooks());
        StringBuilder sb = new StringBuilder();
        for (Loan l : service.loansForMember(currentUser.getUsername())) sb.append(l).append("\n");
        sb.append(String.format("Outstanding fines: R%.2f%n", currentUser.getOutstandingFines()));
        myLoansArea.setText(sb.toString());
    }

    // ---------- Admin module ----------
    private JPanel buildAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleF = new JTextField(10), authorF = new JTextField(10),
                isbnF = new JTextField(8), categoryF = new JTextField(8), copiesF = new JTextField(3);
        JButton addBtn = new JButton("Add Book");
        JLabel status = new JLabel(" ");

        addBtn.addActionListener(e -> {
            try {
                service.addBook(titleF.getText(), authorF.getText(), isbnF.getText(),
                        categoryF.getText(), Integer.parseInt(copiesF.getText()));
                status.setText("Book added.");
                refreshAdminView();
            } catch (NumberFormatException ex) {
                status.setText("Copies must be a number.");
            }
        });

        JPanel addRow = new JPanel();
        addRow.add(new JLabel("Title:")); addRow.add(titleF);
        addRow.add(new JLabel("Author:")); addRow.add(authorF);
        addRow.add(new JLabel("ISBN:")); addRow.add(isbnF);
        addRow.add(new JLabel("Category:")); addRow.add(categoryF);
        addRow.add(new JLabel("Copies:")); addRow.add(copiesF);
        addRow.add(addBtn);

        JTextField deleteIdF = new JTextField(5);
        JButton deleteBtn = new JButton("Delete Book by ID");
        deleteBtn.addActionListener(e -> {
            status.setText(withId(deleteIdF, id -> service.deleteBook(id) ? "Deleted." : "Not found."));
            refreshAdminView();
        });
        JPanel deleteRow = new JPanel();
        deleteRow.add(new JLabel("Book ID:")); deleteRow.add(deleteIdF); deleteRow.add(deleteBtn);

        adminIssuedArea = new JTextArea(10, 45);
        adminIssuedArea.setEditable(false);

        JTextField payUserF = new JTextField(10);
        JButton payBtn = new JButton("Mark Fines Paid");
        payBtn.addActionListener(e -> {
            boolean ok = service.payFines(payUserF.getText().trim());
            status.setText(ok ? "Fines cleared for " + payUserF.getText() : "Member not found.");
            refreshAdminView();
        });
        JPanel payRow = new JPanel();
        payRow.add(new JLabel("Username:")); payRow.add(payUserF); payRow.add(payBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> { currentUser = null; cardLayout.show(cards, "login"); });

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(addRow);
        top.add(deleteRow);

        JPanel south = new JPanel(new GridLayout(3, 1));
        south.add(payRow);
        south.add(status);
        south.add(logoutBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(adminIssuedArea), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAdminView() {
        StringBuilder sb = new StringBuilder("Catalogue:\n");
        for (Book b : service.allBooks()) sb.append(b).append("\n");
        sb.append("\nActive Loans:\n");
        for (Loan l : service.activeLoans()) sb.append(l).append("\n");
        sb.append("\nMembers:\n");
        for (Member m : service.allMembers()) {
            sb.append(String.format("%s (%s) | Fines: R%.2f%n", m.getUsername(), m.getFullName(), m.getOutstandingFines()));
        }
        adminIssuedArea.setText(sb.toString());
    }

    // ---------- helpers ----------
    private interface IntAction<T> { T run(int id); }

    private <T> String withId(JTextField field, IntAction<T> action) {
        try {
            int id = Integer.parseInt(field.getText().trim());
            return String.valueOf(action.run(id));
        } catch (NumberFormatException ex) {
            return "Enter a valid numeric ID.";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryApp().setVisible(true));
    }
}
