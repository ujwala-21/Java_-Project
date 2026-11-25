package com.bookloop.ui;

import com.bookloop.controller.LoginController;
import com.bookloop.model.User;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private final LoginController loginController = new LoginController();

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("BookLoop - Smart Academic Book Exchange");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 580);
        setMinimumSize(new Dimension(840, 520));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainContainer = new JPanel(new GridLayout(1, 2));

        // LEFT PANEL: BRANDING & ARTWORK
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Deep plum to warm mauve vertical gradient
                GradientPaint gp = new GradientPaint(0, 0, UIUtil.SIDEBAR_BG, 0, getHeight(), new Color(0x502A46));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative soft circular glow
                g2.setColor(new Color(0xF4A26122, true));
                g2.fillOval(getWidth() - 150, -50, 300, 300);
                g2.setColor(new Color(0xE76F5118, true));
                g2.fillOval(-80, getHeight() - 200, 260, 260);
                g2.dispose();
            }
        };
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 48, 60, 48));

        JLabel logoIcon = new JLabel("📚");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandName = new JLabel("BookLoop");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 32));
        brandName.setForeground(Color.WHITE);
        brandName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><i>\"Your old books. Someone else's next semester.\"</i></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tagline.setForeground(new Color(0xF4D6C8));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel feature1 = createFeatureItem("✨ Smart Direct & Circular Book Exchange");
        JLabel feature2 = createFeatureItem("💳 Virtual Student Credit Wallet");
        JLabel feature3 = createFeatureItem("🎓 Semester Course Catalog Recommendations");
        JLabel feature4 = createFeatureItem("🤝 Transparent Conditions & Verified Reviews");

        leftPanel.add(logoIcon);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(brandName);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        leftPanel.add(tagline);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 36)));
        leftPanel.add(feature1);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        leftPanel.add(feature2);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        leftPanel.add(feature3);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        leftPanel.add(feature4);
        leftPanel.add(Box.createVerticalGlue());

        JLabel copyLbl = new JLabel("BookLoop Academic Platform v1.0");
        copyLbl.setFont(UIUtil.FONT_CAPTION);
        copyLbl.setForeground(new Color(0xB899B0));
        copyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(copyLbl);

        // RIGHT PANEL: LOGIN FORM
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel welcomeLbl = new JLabel("Welcome back! 👋");
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLbl.setForeground(UIUtil.TEXT_PRIMARY);
        welcomeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = UIUtil.createMutedLabel("Sign in to your campus book exchange account");
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtil.FONT_SMALL_BOLD);
        errorLabel.setForeground(UIUtil.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLbl = UIUtil.createBodyLabel("Email Address");
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField = UIUtil.createTextField("student1@bookloop.com");
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIUtil.INPUT_HEIGHT));

        JLabel pwdLbl = UIUtil.createBodyLabel("Password");
        pwdLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UIUtil.createPasswordField("Enter password");
        passwordField.setText("Student@123");
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIUtil.INPUT_HEIGHT));

        loginBtn = UIUtil.createPrimaryButton("Sign In to BookLoop");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> performLogin());

        // Enter key listener
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        emailField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Register Link
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerLinkPanel.setOpaque(false);
        registerLinkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noAccount = UIUtil.createBodyLabel("Don't have an account?");
        JButton regBtn = UIUtil.createGhostButton("Create student account");
        regBtn.setFont(UIUtil.FONT_BODY_BOLD);
        regBtn.setForeground(UIUtil.ACCENT_CORAL);
        regBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        registerLinkPanel.add(noAccount);
        registerLinkPanel.add(regBtn);

        // Demo Quick-Fill Bar
        JPanel quickBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        quickBar.setOpaque(false);
        quickBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel quickLbl = UIUtil.createMutedLabel("Demo Login:");
        JButton quickStudent = UIUtil.createGhostButton("👤 Student");
        quickStudent.setFont(UIUtil.FONT_SMALL_BOLD);
        quickStudent.addActionListener(e -> {
            emailField.setText("student1@bookloop.com");
            passwordField.setText("Student@123");
        });
        JButton quickAdmin = UIUtil.createGhostButton("🛡️ Admin");
        quickAdmin.setFont(UIUtil.FONT_SMALL_BOLD);
        quickAdmin.addActionListener(e -> {
            emailField.setText("admin@bookloop.com");
            passwordField.setText("Admin@123");
        });
        quickBar.add(quickLbl);
        quickBar.add(quickStudent);
        quickBar.add(quickAdmin);

        rightPanel.add(welcomeLbl);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        rightPanel.add(subLbl);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        rightPanel.add(errorLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(emailLbl);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        rightPanel.add(emailField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        rightPanel.add(pwdLbl);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(loginBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(registerLinkPanel);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(quickBar);

        mainContainer.add(leftPanel);
        mainContainer.add(rightPanel);

        setContentPane(mainContainer);
    }

    private JLabel createFeatureItem(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(0xFDF0EB));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        errorLabel.setText("Authenticating...");
        errorLabel.setForeground(UIUtil.INFO);
        loginBtn.setEnabled(false);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return loginController.handleLogin(email, password);
            }

            @Override
            protected void done() {
                loginBtn.setEnabled(true);
                try {
                    User user = get();
                    if (user != null) {
                        MainDashboardFrame dashboard = new MainDashboardFrame();
                        dashboard.setVisible(true);
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
