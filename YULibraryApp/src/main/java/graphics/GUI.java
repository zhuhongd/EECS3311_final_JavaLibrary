package graphics;

import model.clients.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class GUI {

    private static final String CSV_FILE_PATH = "users.csv";
    private static final String BOOKS_FILE_PATH = "books.csv";
    private static final String CHECKOUT_FILE_PATH = "checkout.csv";
    private static User currentUser;

    public static void main(String[] args) {
        createFilesIfNeeded();
        SwingUtilities.invokeLater(GUI::createAndShowGUI);
    }

    private static void createFilesIfNeeded() {
        createFileIfNeeded(CSV_FILE_PATH);
        createFileIfNeeded(BOOKS_FILE_PATH);
        createFileIfNeeded(CHECKOUT_FILE_PATH);
    }

    private static void createFileIfNeeded(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to create necessary data files.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        showWelcomeScreen();

        JFrame frame = new JFrame("Login App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> showLoginDialog());

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> showRegisterDialog());

        JPanel panel = new JPanel();
        panel.add(loginButton);
        panel.add(registerButton);
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.setSize(300, 200);
        frame.setVisible(true);
    }

    private static void showWelcomeScreen() {
        JOptionPane.showMessageDialog(null, "Welcome to the YU Library", "Welcome", JOptionPane.INFORMATION_MESSAGE);
    }


    private static void showLoginDialog() {
        JDialog loginDialog = new JDialog();
        loginDialog.setTitle("Login");

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (checkCredentials(username, password)) {
                JOptionPane.showMessageDialog(loginDialog, "Login successful!");
                loginDialog.dispose();
                showOptions();
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);

        loginDialog.setContentPane(panel);
        loginDialog.pack();
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);
    }

    private static void showRegisterDialog() {
        JDialog registerDialog = new JDialog();
        registerDialog.setTitle("Register");

        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField idField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        String[] roles = {"Student", "Faculty", "Visitor", "Non-Faculty Staff"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(registerDialog, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (checkCredentials(username, password)) {
                JOptionPane.showMessageDialog(registerDialog, "User already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (FileWriter writer = new FileWriter(CSV_FILE_PATH, true)) {
                writer.append(String.format("%s,%s,%s,%s%n", username, password, idField.getText(), roleComboBox.getSelectedItem()));
                JOptionPane.showMessageDialog(registerDialog, "Registration successful!");
                registerDialog.dispose();
                showOptions();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(registerDialog, "Error occurred while registering!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);
        panel.add(registerButton);

        registerDialog.setContentPane(panel);
        registerDialog.pack();
        registerDialog.setLocationRelativeTo(null);
        registerDialog.setVisible(true);
    }

    private static void showOptions() {
        JFrame optionsFrame = new JFrame("Options");
        optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        optionsFrame.setLayout(new FlowLayout());

        JButton showBooksButton = new JButton("Show Books");
        showBooksButton.addActionListener(e -> showBooks());

        optionsFrame.add(showBooksButton);

        optionsFrame.pack();
        optionsFrame.setLocationRelativeTo(null);
        optionsFrame.setVisible(true);

        JButton showCheckedOutButton = new JButton("Show Checked Out Books");
        showCheckedOutButton.addActionListener(e -> showCheckedOutBooks());

        optionsFrame.add(showCheckedOutButton);

        optionsFrame.pack();
        optionsFrame.setLocationRelativeTo(null);
        optionsFrame.setVisible(true);
    }

    private static void showBooks() {
        JFrame booksFrame = new JFrame("Books");
        booksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultListModel<String> bookListModel = new DefaultListModel<>();
        bookListModel.addElement("Book 1"); // Example books, replace with dynamic loading
        bookListModel.addElement("Book 2");
        bookListModel.addElement("Book 3");
        JList<String> bookList = new JList<>(bookListModel);
        bookList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JButton checkOutButton = new JButton("Check Out Selected");
        checkOutButton.addActionListener(e -> {
            List<String> selectedBooks = bookList.getSelectedValuesList();
            checkOutBooks(selectedBooks);
            JOptionPane.showMessageDialog(booksFrame, "Checked out successfully.");
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(bookList), BorderLayout.CENTER);
        panel.add(checkOutButton, BorderLayout.SOUTH);

        booksFrame.getContentPane().add(panel);

        booksFrame.setSize(400, 300);
        booksFrame.setLocationRelativeTo(null);
        booksFrame.setVisible(true);
    }

    private static void checkOutBooks(List<String> books) {
        try (FileWriter writer = new FileWriter(CHECKOUT_FILE_PATH, true)) {
            for (String book : books) {
                writer.write(currentUser.getUsername() + "," + book + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void showCheckedOutBooks() {
        JFrame checkedOutFrame = new JFrame("Checked Out Books");
        checkedOutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultListModel<String> checkedOutListModel = new DefaultListModel<>();
        // Load checked out books from file
        try (BufferedReader reader = new BufferedReader(new FileReader(CHECKOUT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(currentUser.getUsername())) {
                    checkedOutListModel.addElement(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JList<String> checkedOutList = new JList<>(checkedOutListModel);
        checkedOutFrame.getContentPane().add(new JScrollPane(checkedOutList), BorderLayout.CENTER);

        checkedOutFrame.setSize(400, 200);
        checkedOutFrame.setLocationRelativeTo(null);
        checkedOutFrame.setVisible(true);
    }

    private static boolean checkCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
