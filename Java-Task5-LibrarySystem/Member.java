public class Member {
    private final String username;
    private String password;
    private String fullName;
    private double outstandingFines;
    private final boolean isAdmin;

    public Member(String username, String password, String fullName, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.isAdmin = isAdmin;
        this.outstandingFines = 0.0;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String entered) { return password.equals(entered); }
    public String getFullName() { return fullName; }
    public boolean isAdmin() { return isAdmin; }
    public double getOutstandingFines() { return outstandingFines; }
    public void addFine(double amount) { outstandingFines += amount; }
    public void clearFines() { outstandingFines = 0.0; }
}
