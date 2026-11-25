package com.bookloop.ui;

import com.bookloop.controller.LoginController;
import com.bookloop.model.Student;
import com.bookloop.util.UIUtil;
import com.bookloop.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RegisterFrame extends JFrame {
    private final LoginController loginController = new LoginController();

    private JTextField nameField;
    private JTextField studentIdField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> deptCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> semCombo;
    private JTextField phoneField;
    private JLabel errorLabel;
    private JButton registerBtn;

    public RegisterFrame() {
        setTitle("Join BookLoop - Student Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 680);
        setMinimumSize(new Dimension(740, 620));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtil.BG_PRIMARY);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIUtil.SIDEBAR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        JLabel title = new JLabel("Create Your BookLoop Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Connect with classmates to exchange, buy, rent, or donate textbooks");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0xF4D6C8));

        headerPanel.add(title);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(sub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card (Scrollable)
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(UIUtil.BG_PRIMARY);
        formContainer.setBorder(BorderFactory.createEmptyBorder(20, 36, 20, 36));

        JPanel formCard = UIUtil.createCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setMaximumSize(new Dimension(700, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Error Label
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtil.FONT_SMALL_BOLD);
        errorLabel.setForeground(UIUtil.ERROR);
        formCard.add(errorLabel, gbc);
        gbc.gridwidth = 1;

        // Row 1: Name & Student ID
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(UIUtil.createBodyLabel("Full Name *"), gbc);
        gbc.gridx = 1;
        nameField = UIUtil.createTextField("e.g. Alex Johnson");
        formCard.add(nameField, gbc);

        gbc.gridx = 2;
        formCard.add(UIUtil.createBodyLabel("Student ID *"), gbc);
        gbc.gridx = 3;
        studentIdField = UIUtil.createTextField("e.g. CS2024099");
        formCard.add(studentIdField, gbc);

        // Row 2: Email & Phone
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(UIUtil.createBodyLabel("Campus Email *"), gbc);
        gbc.gridx = 1;
        emailField = UIUtil.createTextField("student@college.edu");
        formCard.add(emailField, gbc);

        gbc.gridx = 2;
        formCard.add(UIUtil.createBodyLabel("Phone Number"), gbc);
        gbc.gridx = 3;
        phoneField = UIUtil.createTextField("10-digit number");
        formCard.add(phoneField, gbc);

        // Row 3: Password & Confirm
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(UIUtil.createBodyLabel("Password *"), gbc);
        gbc.gridx = 1;
        passwordField = UIUtil.createPasswordField("Min 6 characters");
        formCard.add(passwordField, gbc);

        gbc.gridx = 2;
        formCard.add(UIUtil.createBodyLabel("Confirm Password *"), gbc);
        gbc.gridx = 3;
        confirmPasswordField = UIUtil.createPasswordField("Re-type password");
        formCard.add(confirmPasswordField, gbc);

        // Row 4: Department & Year/Semester
        gbc.gridx = 0; gbc.gridy = 4;
        formCard.add(UIUtil.createBodyLabel("Department *"), gbc);
        gbc.gridx = 1;
        deptCombo = UIUtil.createComboBox(new String[]{
            "Computer Science & Engineering",
            "Electronics & Communication",
            "Mechanical Engineering",
            "Civil Engineering",
            "Business Administration",
            "Information Technology",
            "Biotechnology"
        });
        formCard.add(deptCombo, gbc);

        gbc.gridx = 2;
        formCard.add(UIUtil.createBodyLabel("Year & Semester"), gbc);
        gbc.gridx = 3;
        JPanel ysPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        ysPanel.setOpaque(false);
        yearCombo = UIUtil.createComboBox(new String[]{"Year 1", "Year 2", "Year 3", "Year 4"});
        semCombo = UIUtil.createComboBox(new String[]{"Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"});
        ysPanel.add(yearCombo);
        ysPanel.add(semCombo);
        formCard.add(ysPanel, gbc);

        // Row 5: Register Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 10, 10, 10);
        registerBtn = UIUtil.createPrimaryButton("Create Account & Get 50 Free Credits 🎉");
        registerBtn.setPreferredSize(new Dimension(registerBtn.getPreferredSize().width, 42));
        registerBtn.addActionListener(e -> performRegister());
        formCard.add(registerBtn, gbc);

        // Row 6: Back to login
        gbc.gridy = 6;
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setOpaque(false);
        JLabel existLbl = UIUtil.createBodyLabel("Already have an account?");
        JButton signinBtn = UIUtil.createGhostButton("Sign In");
        signinBtn.setFont(UIUtil.FONT_BODY_BOLD);
        signinBtn.setForeground(UIUtil.ACCENT_CORAL);
        signinBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        backPanel.add(existLbl);
        backPanel.add(signinBtn);
        formCard.add(backPanel, gbc);

        formContainer.add(formCard);
        JScrollPane scrollPane = UIUtil.createScrollPane(formContainer);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void performRegister() {
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String email = emailField.getText().trim();
        String pwd = new String(passwordField.getPassword());
        String confirmPwd = new String(confirmPasswordField.getPassword());
        String dept = (String) deptCombo.getSelectedItem();
        int year = yearCombo.getSelectedIndex() + 1;
        int sem = semCombo.getSelectedIndex() + 1;
        String phone = phoneField.getText().trim();

        registerBtn.setEnabled(false);
        errorLabel.setText("Creating your account...");
        errorLabel.setForeground(UIUtil.INFO);

        SwingWorker<Student, Void> worker = new SwingWorker<>() {
            @Override
            protected Student doInBackground() throws Exception {
                return loginController.handleRegister(name, studentId, email, pwd, confirmPwd, dept, year, sem, phone);
            }

            @Override
            protected void done() {
                registerBtn.setEnabled(true);
                try {
                    Student student = get();
                    if (student != null) {
                        UIUtil.showSuccess(RegisterFrame.this, "Welcome to BookLoop, " + student.getName() + "!\nYour account was created with 50 initial wallet credits.");
                        new MainDashboardFrame().setVisible(true);
                        dispose();
                    }
                } catch (Exception e) {
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    errorLabel.setText(msg);
                    errorLabel.setForeground(UIUtil.ERROR);
                }
            }
        };
        worker.execute();
    }
}
