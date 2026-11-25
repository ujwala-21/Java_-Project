package com.bookloop.ui.student;

import com.bookloop.dao.UserDAO;
import com.bookloop.model.Student;
import com.bookloop.service.AuthenticationService;
import com.bookloop.util.DateUtil;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private final AuthenticationService authService = new AuthenticationService();
    private final UserDAO userDAO = new UserDAO();

    private JTextField nameField;
    private JTextField studentIdField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> deptCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> semCombo;

    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadStudentData();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("My Student Profile");
        JLabel subLbl = UIUtil.createMutedLabel("Manage your academic details, contact information, and security");
        headerPanel.add(titleLbl, BorderLayout.NORTH);
        headerPanel.add(subLbl, BorderLayout.SOUTH);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(headerPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Profile Details Card
        JPanel detailsCard = UIUtil.createCard();
        detailsCard.setLayout(new GridBagLayout());
        detailsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsCard.setMaximumSize(new Dimension(850, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Section Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel section1 = UIUtil.createHeadingLabel("Personal & Academic Information");
        section1.setForeground(UIUtil.ACCENT_CORAL);
        detailsCard.add(section1, gbc);

        gbc.gridwidth = 1;

        // Row 1: Full Name & Student ID
        gbc.gridx = 0; gbc.gridy = 1;
        detailsCard.add(UIUtil.createBodyLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = UIUtil.createTextField("Enter your name");
        detailsCard.add(nameField, gbc);

        gbc.gridx = 2;
        detailsCard.add(UIUtil.createBodyLabel("Student ID:"), gbc);
        gbc.gridx = 3;
        studentIdField = UIUtil.createTextField("Student ID");
        studentIdField.setEditable(false);
        studentIdField.setBackground(new Color(0xF4EFEA));
        detailsCard.add(studentIdField, gbc);

        // Row 2: Email & Phone
        gbc.gridx = 0; gbc.gridy = 2;
        detailsCard.add(UIUtil.createBodyLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        emailField = UIUtil.createTextField("Email");
        emailField.setEditable(false);
        emailField.setBackground(new Color(0xF4EFEA));
        detailsCard.add(emailField, gbc);

        gbc.gridx = 2;
        detailsCard.add(UIUtil.createBodyLabel("Phone Number:"), gbc);
        gbc.gridx = 3;
        phoneField = UIUtil.createTextField("10-digit mobile number");
        detailsCard.add(phoneField, gbc);

        // Row 3: Dept, Year & Semester
        gbc.gridx = 0; gbc.gridy = 3;
        detailsCard.add(UIUtil.createBodyLabel("Department:"), gbc);
        gbc.gridx = 1;
        deptCombo = UIUtil.createComboBox(new String[]{"Computer Science & Engineering", "Electronics & Communication", "Mechanical Engineering", "Civil Engineering", "Business Administration", "Information Technology", "Biotechnology"});
        detailsCard.add(deptCombo, gbc);

        gbc.gridx = 2;
        detailsCard.add(UIUtil.createBodyLabel("Year & Semester:"), gbc);
        gbc.gridx = 3;
        JPanel yearSemPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        yearSemPanel.setOpaque(false);
        yearCombo = UIUtil.createComboBox(new String[]{"Year 1", "Year 2", "Year 3", "Year 4"});
        semCombo = UIUtil.createComboBox(new String[]{"Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"});
        yearSemPanel.add(yearCombo);
        yearSemPanel.add(semCombo);
        detailsCard.add(yearSemPanel, gbc);

        // Row 4: Save Button
        gbc.gridx = 3; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        JButton saveBtn = UIUtil.createPrimaryButton("Save Profile Changes");
        saveBtn.addActionListener(e -> saveProfileChanges());
        detailsCard.add(saveBtn, gbc);

        container.add(detailsCard);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Security / Password Card
        JPanel securityCard = UIUtil.createCard();
        securityCard.setLayout(new GridBagLayout());
        securityCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        securityCard.setMaximumSize(new Dimension(850, 300));

        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(8, 12, 8, 12);
        sgbc.fill = GridBagConstraints.HORIZONTAL;

        sgbc.gridx = 0; sgbc.gridy = 0; sgbc.gridwidth = 2;
        JLabel section2 = UIUtil.createHeadingLabel("Security & Password");
        section2.setForeground(UIUtil.ACCENT_CORAL);
        securityCard.add(section2, sgbc);

        sgbc.gridwidth = 1;

        sgbc.gridx = 0; sgbc.gridy = 1;
        securityCard.add(UIUtil.createBodyLabel("Current Password:"), sgbc);
        sgbc.gridx = 1;
        currentPasswordField = UIUtil.createPasswordField("Current password");
        securityCard.add(currentPasswordField, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 2;
        securityCard.add(UIUtil.createBodyLabel("New Password:"), sgbc);
        sgbc.gridx = 1;
        newPasswordField = UIUtil.createPasswordField("At least 6 characters");
        securityCard.add(newPasswordField, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 3;
        securityCard.add(UIUtil.createBodyLabel("Confirm New Password:"), sgbc);
        sgbc.gridx = 1;
        confirmPasswordField = UIUtil.createPasswordField("Re-enter new password");
        securityCard.add(confirmPasswordField, sgbc);

        sgbc.gridx = 1; sgbc.gridy = 4; sgbc.anchor = GridBagConstraints.EAST;
        JButton changePwdBtn = UIUtil.createSecondaryButton("Update Password");
        changePwdBtn.addActionListener(e -> updatePassword());
        securityCard.add(changePwdBtn, sgbc);

        container.add(securityCard);

        JScrollPane scrollPane = UIUtil.createScrollPane(container);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStudentData() {
        Student student = SessionManager.getInstance().getCurrentStudent();
        if (student != null) {
            nameField.setText(student.getName());
            studentIdField.setText(student.getStudentId());
            emailField.setText(student.getEmail());
            phoneField.setText(student.getPhone() != null ? student.getPhone() : "");

            if (student.getDepartment() != null) {
                deptCombo.setSelectedItem(student.getDepartment());
            }
            if (student.getYearOfStudy() >= 1 && student.getYearOfStudy() <= 4) {
                yearCombo.setSelectedIndex(student.getYearOfStudy() - 1);
            }
            if (student.getSemester() >= 1 && student.getSemester() <= 8) {
                semCombo.setSelectedIndex(student.getSemester() - 1);
            }
        }
    }

    private void saveProfileChanges() {
        Student current = SessionManager.getInstance().getCurrentStudent();
        if (current == null) return;

        current.setName(nameField.getText().trim());
        current.setPhone(phoneField.getText().trim());
        current.setDepartment((String) deptCombo.getSelectedItem());
        current.setYearOfStudy(yearCombo.getSelectedIndex() + 1);
        current.setSemester(semCombo.getSelectedIndex() + 1);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.updateProfile(current);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ProfilePanel.this, "Profile updated successfully!");
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ProfilePanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updatePassword() {
        String currentPwd = new String(currentPasswordField.getPassword());
        String newPwd = new String(newPasswordField.getPassword());
        String confirmPwd = new String(confirmPasswordField.getPassword());

        int userId = SessionManager.getInstance().getCurrentUserId();

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.changePassword(userId, currentPwd, newPwd, confirmPwd);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ProfilePanel.this, "Password updated successfully!");
                        currentPasswordField.setText("");
                        newPasswordField.setText("");
                        confirmPasswordField.setText("");
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ProfilePanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
