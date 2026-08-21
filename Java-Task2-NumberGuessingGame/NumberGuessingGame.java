import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NumberGuessingGame extends JFrame {

    private final Random random = new Random();
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    private int target, maxAttempts, attemptsUsed, round = 1;
    private JLabel promptLabel, feedbackLabel, attemptsLabel;
    private JTextField guessField;
    private JTextArea historyArea;

    public NumberGuessingGame() {
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);

        cards.add(buildSetupPanel(), "setup");
        cards.add(buildGamePanel(), "game");
        add(cards);
        cardLayout.show(cards, "setup");
    }

    private JPanel buildSetupPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Choose a difficulty");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton easyBtn = new JButton("Easy (1–50, 10 attempts)");
        JButton medBtn = new JButton("Medium (1–100, 7 attempts)");
        JButton hardBtn = new JButton("Hard (1–200, 5 attempts)");

        easyBtn.addActionListener(e -> startGame(50, 10));
        medBtn.addActionListener(e -> startGame(100, 7));
        hardBtn.addActionListener(e -> startGame(200, 5));

        for (JButton b : new JButton[]{easyBtn, medBtn, hardBtn}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(250, 35));
        }

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(easyBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(medBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(hardBtn);
        return panel;
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        promptLabel = new JLabel("Guess a number", SwingConstants.CENTER);
        promptLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        attemptsLabel = new JLabel("Attempts: 0/0", SwingConstants.CENTER);
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        guessField = new JTextField();
        JButton guessBtn = new JButton("Guess");
        guessBtn.addActionListener(e -> submitGuess());
        guessField.addActionListener(e -> submitGuess()); // Enter key submits too

        JPanel inputRow = new JPanel(new BorderLayout(5, 5));
        inputRow.add(guessField, BorderLayout.CENTER);
        inputRow.add(guessBtn, BorderLayout.EAST);

        JPanel top = new JPanel(new GridLayout(3, 1));
        top.add(promptLabel);
        top.add(attemptsLabel);
        top.add(feedbackLabel);

        historyArea = new JTextArea(6, 20);
        historyArea.setEditable(false);

        JButton newRoundBtn = new JButton("New Round (change difficulty)");
        newRoundBtn.addActionListener(e -> cardLayout.show(cards, "setup"));

        panel.add(top, BorderLayout.NORTH);
        panel.add(inputRow, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        bottom.add(newRoundBtn, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void startGame(int max, int attempts) {
        target = random.nextInt(max) + 1;
        maxAttempts = attempts;
        attemptsUsed = 0;
        promptLabel.setText("I'm thinking of a number between 1 and " + max);
        attemptsLabel.setText("Attempts: 0/" + maxAttempts);
        feedbackLabel.setText(" ");
        guessField.setText("");
        cardLayout.show(cards, "game");
        guessField.requestFocusInWindow();
    }

    private void submitGuess() {
        int guess;
        try {
            guess = Integer.parseInt(guessField.getText().trim());
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Enter a whole number.");
            return;
        }
        guessField.setText("");
        attemptsUsed++;
        attemptsLabel.setText("Attempts: " + attemptsUsed + "/" + maxAttempts);

        if (guess == target) {
            feedbackLabel.setForeground(new Color(0, 130, 0));
            feedbackLabel.setText("Correct! You got it in " + attemptsUsed + " attempts.");
            logRound(true);
            offerPlayAgain();
        } else if (attemptsUsed >= maxAttempts) {
            feedbackLabel.setForeground(Color.RED);
            feedbackLabel.setText("You Lost! The number was " + target + ".");
            logRound(false);
            offerPlayAgain();
        } else if (guess < target) {
            feedbackLabel.setForeground(Color.BLUE);
            feedbackLabel.setText("Too Low!");
        } else {
            feedbackLabel.setForeground(Color.BLUE);
            feedbackLabel.setText("Too High!");
        }
    }

    private void logRound(boolean won) {
        historyArea.append("Round " + round + " — " + (won ? "guessed in " + attemptsUsed + " attempts" : "not solved") + "\n");
        round++;
    }

    private void offerPlayAgain() {
        int choice = JOptionPane.showConfirmDialog(this, "Play again?", "Round over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            cardLayout.show(cards, "setup");
        } else {
            JOptionPane.showMessageDialog(this, "Thanks for playing!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberGuessingGame().setVisible(true));
    }
}
