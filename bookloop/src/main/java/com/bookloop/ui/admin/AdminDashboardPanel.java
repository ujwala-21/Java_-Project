package com.bookloop.ui.admin;

import com.bookloop.controller.AdminController;
import com.bookloop.ui.components.StatCard;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {
    private final AdminController adminController = new AdminController();
    private final Consumer<String> onNavigate;

    private StatCard studentsCard;
    private StatCard listingsCard;
    private StatCard exchangesCard;
    private StatCard reservationsCard;
    private StatCard successRateCard;
    private StatCard donatedCard;

    public AdminDashboardPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Administrator Control Center 🛡️");
        JLabel subLbl = UIUtil.createMutedLabel("Campus-wide monitoring, user management, and textbook exchange analytics");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 18)));

        // Stat Cards Grid (6 Metric Cards)
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(860, 240));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        studentsCard = new StatCard("👥", "REGISTERED STUDENTS", "0", "Enrolled students", UIUtil.ACCENT_PEACH);
        listingsCard = new StatCard("📚", "AVAILABLE TEXTBOOKS", "0", "Live listings", UIUtil.ACCENT_CORAL);
        exchangesCard = new StatCard("🤝", "COMPLETED EXCHANGES", "0", "Successful handovers", UIUtil.SUCCESS);
        reservationsCard = new StatCard("⏳", "ACTIVE RESERVATIONS", "0", "In 7-day pickup window", UIUtil.WARNING);
        successRateCard = new StatCard("📈", "EXCHANGE SUCCESS RATE", "100%", "Fulfillment efficiency", UIUtil.INFO);
        donatedCard = new StatCard("🎁", "DONATED BOOKS", "0", "Free campus reuses", UIUtil.ACCENT_MAUVE);

        statsGrid.add(studentsCard);
        statsGrid.add(listingsCard);
        statsGrid.add(exchangesCard);
        statsGrid.add(reservationsCard);
        statsGrid.add(successRateCard);
        statsGrid.add(donatedCard);

        container.add(statsGrid);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Quick Administrative Actions Card
        JPanel quickActionsCard = UIUtil.createCard();
        quickActionsCard.setLayout(new BorderLayout());
        quickActionsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActionsCard.setMaximumSize(new Dimension(860, 120));

        JLabel qaTitle = UIUtil.createHeadingLabel("Quick Administrative Management");
        qaTitle.setForeground(UIUtil.ACCENT_CORAL);
        quickActionsCard.add(qaTitle, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        btnRow.setOpaque(false);

        JButton manageUsersBtn = UIUtil.createPrimaryButton("Manage Students 👥");
        manageUsersBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("STUDENTS"); });

        JButton manageListingsBtn = UIUtil.createSecondaryButton("Moderate Listings 📋");
        manageListingsBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("LISTINGS"); });

        JButton analyticsBtn = UIUtil.createSecondaryButton("View Visual Analytics 📊");
        analyticsBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("ANALYTICS"); });

        JButton refreshStatsBtn = UIUtil.createGhostButton("🔄 Refresh Data");
        refreshStatsBtn.addActionListener(e -> loadStatistics());

        btnRow.add(manageUsersBtn);
        btnRow.add(manageListingsBtn);
        btnRow.add(analyticsBtn);
        btnRow.add(refreshStatsBtn);
        quickActionsCard.add(btnRow, BorderLayout.CENTER);

        container.add(quickActionsCard);

        JScrollPane scrollPane = UIUtil.createScrollPane(container);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadStatistics() {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                return adminController.getDashboardStats();
            }

            @Override
            protected void done() {
                try {
                    Map<String, Object> stats = get();
                    studentsCard.setValue(String.valueOf(stats.getOrDefault("totalStudents", 0)));
                    listingsCard.setValue(String.valueOf(stats.getOrDefault("availableListings", 0)));
                    exchangesCard.setValue(String.valueOf(stats.getOrDefault("completedExchanges", 0)));
                    reservationsCard.setValue(String.valueOf(stats.getOrDefault("activeReservations", 0)));
                    successRateCard.setValue(stats.getOrDefault("successRate", "100") + "%");
                    donatedCard.setValue(String.valueOf(stats.getOrDefault("booksDonated", 0)));
                } catch (Exception ex) {
                    UIUtil.showError(AdminDashboardPanel.this, "Failed to load admin stats: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
