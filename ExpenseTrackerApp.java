import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


class Expense {
    private String description;
    private Date date;
    private String category;
    private double amount;
    

    public Expense(String description, Date date, String category, double amount) {
        this.description = description;
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getFormattedDescription() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return description + " (" + category + ") - Date: " + dateFormat.format(date) + ", Amount: RS " + amount;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return description + " (" + category + ") - Date: " + dateFormat.format(date) + ", Amount: RS " + amount;
    }
}

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

public class ExpenseTrackerApp {
    private JFrame frame;
    private JTextField usernameField, descriptionField, amountField, dateField, categoryField;
    private JPasswordField passwordField;
    private JTextArea expenseArea;
    private ArrayList<Expense> expenses;
    private User currentUser;

    private static final String CREDENTIALS_FILE = "credentials.txt";
    private static final String EXPENSES_FILE_PREFIX = "expenses_";
    private static final String TOTALS_FILE = "totals.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ExpenseTrackerApp();
        });
    }

    public ExpenseTrackerApp() {
        initialize();
        loadExpenses();
    }

    private void initialize() {
        frame = new JFrame("Expense Tracker");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        expenses = new ArrayList<>();

        JPanel loginPanel = createLoginPanel();
        JPanel expensePanel = createExpensePanel();

        frame.add(loginPanel, BorderLayout.NORTH);
        frame.add(expensePanel, BorderLayout.CENTER);

        frame.setVisible(true);
           // Set default values for input fields
         setDefaultValues();
        
    }

    private void setDefaultValues() {
        // Set the current date as the default value for the date field
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(dateFormat.format(currentDate));
    
        // Set other default values for input fields (if needed)
        descriptionField.setText("");
        categoryField.setText("");
        amountField.setText("");
    }
    

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(173, 216, 230)); // Light Blue background

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 5);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        return panel;
    }
    private JPanel createSortAndFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
    
        JButton sortByDateButton = new JButton("Sort by Date");
        JButton sortByAmountButton = new JButton("Sort by Amount");
        JButton sortByCategoryButton = new JButton("Sort by Category");
    
        sortByDateButton.addActionListener(e -> {
            expenses.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
            updateExpenseArea();
        });
    
        sortByAmountButton.addActionListener(e -> {
            expenses.sort((e1, e2) -> Double.compare(e1.getAmount(), e2.getAmount()));
            updateExpenseArea();
        });
    
        sortByCategoryButton.addActionListener(e -> {
            expenses.sort((e1, e2) -> e1.getCategory().compareTo(e2.getCategory()));
            updateExpenseArea();
        });
    
        panel.add(sortByDateButton, BorderLayout.WEST);
        panel.add(sortByAmountButton, BorderLayout.CENTER);
        panel.add(sortByCategoryButton, BorderLayout.EAST);
    
        return panel;
    }
    private JPanel createExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Set the background color of the entire panel to light gray
        panel.setBackground(new Color(192, 192, 192)); // Light Gray background
        
        expenseArea = new JTextArea();
        
        // Set the background color of the expense area to light gray
        expenseArea.setBackground(new Color(220, 220, 220)); // Light Gray background
        
        JScrollPane scrollPane = new JScrollPane(expenseArea);
    
        JPanel sortAndFilterPanel = createSortAndFilterPanel();
        JPanel expenseInputPanel = createExpenseInputPanel();
    
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(expenseInputPanel, BorderLayout.SOUTH);
        panel.add(sortAndFilterPanel, BorderLayout.NORTH);
    
        return panel;
    }
    
    
    // Other methods...
    

private void filterExpensesByCategory(String filterCategory) {
    ArrayList<Expense> filteredExpenses = new ArrayList<>();
    for (Expense expense : expenses) {
        if (expense.getCategory().equalsIgnoreCase(filterCategory)) {
            filteredExpenses.add(expense);
        }
    }
    expenses = filteredExpenses;
    updateExpenseArea();
}


    private JPanel createExpenseInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(173, 216, 230)); // Light Yellow background
        

        JLabel descriptionLabel = new JLabel("Description:");
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel amountLabel = new JLabel("Amount:");

        descriptionField = new JTextField(20);
        dateField = new JTextField(20);
        categoryField = new JTextField(20);
        amountField = new JTextField(20);

        JButton addExpenseButton = new JButton("Add Expense");
        JButton deleteExpenseButton = new JButton("Delete Selected Expense");
        

        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        deleteExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteExpense();
            }
        });
        

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 5);
        panel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(amountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(addExpenseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(deleteExpenseButton, gbc);

        return panel;
    }

    private void login() {
        String enteredUsername = usernameField.getText();
        char[] enteredPassword = passwordField.getPassword();
    
        if (isValidUser(enteredUsername, enteredPassword)) {
            currentUser = new User(enteredUsername, new String(enteredPassword));
            loadExpenses(); // Load expenses for the current user
            updateExpenseArea();
        } else {
            if (registerNewUserAttempt()) {
                login(); // Recursive call to login after successful registration
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password");
            }
        }
    
        // Clear the password field after processing
        passwordField.setText("");
    }
    
    

    private boolean registerNewUserAttempt() {
        int option = JOptionPane.showConfirmDialog(frame, "User not found. Do you want to register as a new user?",
                "New User Registration", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            registerNewUser();
            return true;
        }

        return false;
    }

    private void registerNewUser() {
        String newUsername = usernameField.getText();
        char[] newPassword = passwordField.getPassword();

        if (newUsername.isEmpty() || newPassword.length == 0) {
            JOptionPane.showMessageDialog(frame, "Please enter both username and password for registration");
            return;
        }

        if (userExists(newUsername)) {
            JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different username.");
            return;
        }

        saveNewUserCredentials(newUsername, new String(newPassword));

        JOptionPane.showMessageDialog(frame, "Registration successful! You can now log in with your new credentials.");
    }

    private boolean userExists(String newUsername) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                if (newUsername.equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void saveNewUserCredentials(String newUsername, String newPassword) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE, true))) {
            writer.write(newUsername + "," + newPassword);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUser(String enteredUsername, char[] enteredPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                String password = parts[1];
                if (enteredUsername.equals(username) && new String(enteredPassword).equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void loadExpenses() {
        expenses.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(EXPENSES_FILE_PREFIX + currentUser.getUsername()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String description = parts[0];
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(parts[1]);
                String category = parts[2];
                double amount = Double.parseDouble(parts[3]);
                expenses.add(new Expense(description, date, category, amount));
            }
        } catch (IOException | ParseException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    
    

    private void updateExpenseArea() {
        expenseArea.setText("");

        // Add column headers with fixed width
        expenseArea.append(String.format("%-20s %-15s %-15s %-15s %-10s\n",
                "Description", "Category", "Date", "Amount", "Running Total"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Map to store total amount for each category and date
        Map<String, Map<String, Double>> categoryDateTotalMap = new HashMap<>();

        // Variable to store grand total
        double grandTotal = 0.0;

        for (Expense expense : expenses) {
            // Append each expense with details in separate columns
            expenseArea.append(String.format("%-20s %-15s %-15s %-15s %-10s\n",
                    expense.getDescription(),
                    expense.getCategory(),
                    dateFormat.format(expense.getDate()),
                    "RS " + expense.getAmount(),
                    "RS " + (grandTotal += expense.getAmount())));

            // Update total amount for the category and date
            categoryDateTotalMap
                    .computeIfAbsent(expense.getCategory(), k -> new HashMap<>())
                    .merge(dateFormat.format(expense.getDate()), expense.getAmount(), Double::sum);
        }

        // Display total amount for each category and date
        expenseArea.append("\nTotal Amount for Each Category and Date:\n");
        for (Map.Entry<String, Map<String, Double>> categoryEntry : categoryDateTotalMap.entrySet()) {
            expenseArea.append(String.format("%-15s\n", categoryEntry.getKey()));
            for (Map.Entry<String, Double> dateEntry : categoryEntry.getValue().entrySet()) {
                expenseArea.append(String.format("  %-15s: RS %.2f\n", dateEntry.getKey(), dateEntry.getValue()));
            }
        }

        // Display grand total
        expenseArea.append(String.format("\nGrand Total: RS %.2f\n", grandTotal));

        // Save total expenses data to file
        saveTotalExpenses(categoryDateTotalMap, grandTotal);
    }

    
    private void saveTotalExpenses(Map<String, Map<String, Double>> categoryDateTotalMap, double grandTotal) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TOTALS_FILE))) {
            writer.write("Category,Date,Amount\n");
            for (Map.Entry<String, Map<String, Double>> categoryEntry : categoryDateTotalMap.entrySet()) {
                for (Map.Entry<String, Double> dateEntry : categoryEntry.getValue().entrySet()) {
                    writer.write(categoryEntry.getKey() + "," + dateEntry.getKey() + "," + dateEntry.getValue());
                    writer.newLine();
                }
            }
            writer.write("Grand Total,, " + String.valueOf(grandTotal));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTotalExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TOTALS_FILE))) {
            String line;
            Map<String, Map<String, Double>> categoryDateTotalMap = new HashMap<>();
            double grandTotal = 0.0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String category = parts[0];
                    String date = parts[1];
                    double amount = Double.parseDouble(parts[2]);

                    categoryDateTotalMap
                            .computeIfAbsent(category, k -> new HashMap<>())
                            .put(date, amount);

                    if (!date.isEmpty()) {
                        grandTotal += amount;
                    }
                }
            }

            // Display total expenses data in the expense area
            expenseArea.append("\nTotal Amount for Each Category and Date (Loaded from File):\n");
            for (Map.Entry<String, Map<String, Double>> categoryEntry : categoryDateTotalMap.entrySet()) {
                expenseArea.append(String.format("%-15s\n", categoryEntry.getKey()));
                for (Map.Entry<String, Double> dateEntry : categoryEntry.getValue().entrySet()) {
                    expenseArea.append(String.format("  %-15s: RS %.2f\n", dateEntry.getKey(), dateEntry.getValue()));
                }
            }

            // Display loaded grand total
            expenseArea.append(String.format("\nLoaded Grand Total: RS %.2f\n", grandTotal));

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // ... (unchanged)

    private void addExpense() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please log in first");
            return;
        }
    
        String description = descriptionField.getText();
        String dateText = dateField.getText();
        String category = categoryField.getText();
        String amountText = amountField.getText();
    
        if (description.isEmpty() || dateText.isEmpty() || category.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter all fields for the expense");
            return;
        }
    
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateText);
            double amount = Double.parseDouble(amountText);
            Expense expense = new Expense(description, date, category, amount);
            expenses.add(expense);
            updateExpenseArea();
            saveExpenses();
            clearExpenseInputFields();
            
            // Set default values for input fields after processing an input
            setDefaultValues();
        } catch (ParseException | NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid date or amount format");
        }
    }
    

    private void deleteExpense() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please log in first");
            return;
        }
    
        int selectionStart = expenseArea.getSelectionStart();
        int selectionEnd = expenseArea.getSelectionEnd();
    
        if (selectionStart != selectionEnd) {
            String selectedText = expenseArea.getSelectedText();
    
            // Use iterator to safely remove element during iteration
            for (java.util.Iterator<Expense> iterator = expenses.iterator(); iterator.hasNext(); ) {
                Expense expense = iterator.next();
                if (selectedText.trim().startsWith(expense.getDescription())) {
                    iterator.remove();
                    updateExpenseArea();
                    saveExpenses();
                    return; // Exit the loop once we find and remove the expense
                }
            }
        }
    }
    
    
    
private String extractDescriptionFromFormattedText(String formattedText) {
    // Extract description from the formatted text
    int index = formattedText.indexOf(" - Date:");
    if (index != -1) {
        return formattedText.substring(0, index).trim();
    }
    return formattedText.trim();
}

    
    
private Expense findExpenseByFormattedDescription(String formattedDescription) {
    for (Expense expense : expenses) {
        if (expense.getFormattedDescription().equals(formattedDescription)) {
            return expense;
        }
    }
    return null;
}

    
    
private void saveExpenses() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXPENSES_FILE_PREFIX + currentUser.getUsername()))) {
        for (Expense expense : expenses) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            writer.write(expense.getDescription() + "," + dateFormat.format(expense.getDate()) +
                    "," + expense.getCategory() + "," + expense.getAmount());
            writer.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    private void clearExpenseInputFields() {
        descriptionField.setText("");
        dateField.setText("");
        categoryField.setText("");
        amountField.setText("");
    }
}
