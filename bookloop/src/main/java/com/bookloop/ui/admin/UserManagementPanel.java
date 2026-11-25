package com.bookloop.ui.admin;

import com.bookloop.controller.AdminController;
import com.bookloop.model.Student;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final AdminController adminController = new AdminController();

    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private List<Student> students = new ArrayList<>();
    private JLabel countLabel;

    public UserManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadStudents();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Student Directory & Account Moderation 👥");
        countLabel = UIUtil.createMutedLabel("Manage registered students, academic departments, and account active statuses");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(countLabel, BorderLayout.SOUTH);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 16)));

        // Action Toolbar
        JPanel tb = UIUtil.createCard();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        tb.setAlignmentX(Component.LEFT_ALIGNMENT);
        tb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadStudents());
        tb.add(refreshBtn);

        JButton toggleStatusBtn = UIUtil.createPrimaryButton("Toggle Active / Deactivate ⚠️");
        toggleStatusBtn.addActionListener(e -> toggleSelectedStudent());
        tb.add(toggleStatusBtn);

        container.add(tb);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table Card
        JPanel tableCard = UIUtil.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"User ID", "Name", "Student ID", "Email", "Department", "Year / Sem", "Wallet Balance", "Active Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        studentsTable = new JTable(tableModel);
        UIUtil.styleTable(studentsTable);
        studentsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        studentsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        studentsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        studentsTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        studentsTable.getColumnModel().getColumn(4).setPreferredWidth(160);
        studentsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        studentsTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        studentsTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane sp = UIUtil.createScrollPane(studentsTable);
        sp.setPreferredSize(new Dimension(850, 420));
        tableCard.add(sp, BorderLayout.CENTER);

        container.add(tableCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    public void loadStudents() {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return adminController.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    students = get();
                    tableModel.setRowCount(0);
                    for (Student s : students) {
                        String bal = s.getWallet() != null ? s.getWallet().getBalance() + " Credits" : "0.00";
                        String statusStr = s.isActive() ? "Active ✅" : "Suspended 🚫";
                        tableModel.addRow(new Object[]{
                            s.getId(),
                            s.getName(),
                            s.getStudentId(),
                            s.getEmail(),
                            s.getDepartment(),
                            "Y" + s.getYearOfStudy() + " / S" + s.getSemester(),
                            bal,
                            statusStr
                        });
                    }
                    countLabel.setText("Total registered students: " + students.size());
                } catch (Exception ex) {
                    UIUtil.showError(UserManagementPanel.this, "Failed to load students: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void toggleSelectedStudent() {
        int row = studentsTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a student from the table first.");
            return;
        }

        Student s = students.get(row);
        String action = s.isActive() ? "Deactivate" : "Re-activate";

        if (!UIUtil.showConfirm(this, action + " student account for " + s.getName() + " (" + s.getStudentId() + ")?")) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return adminController.toggleStudentStatus(s.getId(), s.isActive());
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(UserManagementPanel.this, "Account status updated.");
                        loadStudents();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(UserManagementPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
