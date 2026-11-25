package com.bookloop.ui.admin;

import com.bookloop.controller.AdminController;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsPanel extends JPanel {
    private final AdminController adminController = new AdminController();

    private Map<String, Integer> demandMap = new LinkedHashMap<>();
    private Map<String, Integer> typeMap = new LinkedHashMap<>();
    private Map<String, Integer> condMap = new LinkedHashMap<>();

    private JPanel demandChartPanel;
    private JPanel typeChartPanel;
    private JPanel condChartPanel;

    public AnalyticsPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadAnalyticsData();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Campus Book Demand & Marketplace Analytics 📊");
        JLabel subLbl = UIUtil.createMutedLabel("Real-time aggregate data on textbook reuse rates, student demand, and listing qualities");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 18)));

        // Refresh Bar
        JPanel tb = UIUtil.createCard();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        tb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        tb.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh Analytics");
        refreshBtn.addActionListener(e -> loadAnalyticsData());
        tb.add(refreshBtn);
        container.add(tb);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // ROW 1: MOST WANTED BOOKS CHART (Demand Analytics)
        JPanel demandCard = UIUtil.createCard();
        demandCard.setLayout(new BorderLayout(0, 10));
        demandCard.setMaximumSize(new Dimension(860, 260));
        demandCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel demandTitle = UIUtil.createHeadingLabel("🔥 Highest Demand Textbooks (Student Wishlist Requests)");
        demandTitle.setForeground(UIUtil.ACCENT_CORAL);
        demandCard.add(demandTitle, BorderLayout.NORTH);

        demandChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHorizontalBarChart(g, demandMap, UIUtil.ACCENT_CORAL);
            }
        };
        demandChartPanel.setBackground(Color.WHITE);
        demandChartPanel.setPreferredSize(new Dimension(800, 180));
        demandCard.add(demandChartPanel, BorderLayout.CENTER);
        container.add(demandCard);
        container.add(Box.createRigidArea(new Dimension(0, 16)));

        // ROW 2: SPLIT CHARTS (Type Breakdown & Condition Breakdown)
        JPanel splitRow = new JPanel(new GridLayout(1, 2, 16, 0));
        splitRow.setOpaque(false);
        splitRow.setMaximumSize(new Dimension(860, 240));
        splitRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Chart 2: Listing Types
        JPanel typeCard = UIUtil.createCard();
        typeCard.setLayout(new BorderLayout(0, 10));
        JLabel typeTitle = UIUtil.createHeadingLabel("📦 Distribution by Listing Type");
        typeTitle.setForeground(UIUtil.ACCENT_PEACH);
        typeCard.add(typeTitle, BorderLayout.NORTH);

        typeChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHorizontalBarChart(g, typeMap, UIUtil.ACCENT_PEACH);
            }
        };
        typeChartPanel.setBackground(Color.WHITE);
        typeCard.add(typeChartPanel, BorderLayout.CENTER);
        splitRow.add(typeCard);

        // Chart 3: Physical Condition
        JPanel condCard = UIUtil.createCard();
        condCard.setLayout(new BorderLayout(0, 10));
        JLabel condTitle = UIUtil.createHeadingLabel("✨ Book Physical Conditions");
        condTitle.setForeground(UIUtil.SUCCESS);
        condCard.add(condTitle, BorderLayout.NORTH);

        condChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHorizontalBarChart(g, condMap, UIUtil.SUCCESS);
            }
        };
        condChartPanel.setBackground(Color.WHITE);
        condCard.add(condChartPanel, BorderLayout.CENTER);
        splitRow.add(condCard);

        container.add(splitRow);

        JScrollPane scrollPane = UIUtil.createScrollPane(container);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void drawHorizontalBarChart(Graphics g, Map<String, Integer> data, Color barColor) {
        if (data == null || data.isEmpty()) {
            g.setColor(UIUtil.TEXT_SECONDARY);
            g.setFont(UIUtil.FONT_BODY);
            g.drawString("No data records available yet", 30, 60);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int maxVal = data.values().stream().max(Integer::compareTo).orElse(1);
        if (maxVal == 0) maxVal = 1;

        int y = 20;
        int barHeight = 22;
        int maxBarWidth = getWidth() - 260;
        if (maxBarWidth < 100) maxBarWidth = 100;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String label = entry.getKey();
            if (label.length() > 24) label = label.substring(0, 21) + "...";
            int val = entry.getValue();
            int w = (int) (((double) val / maxVal) * maxBarWidth);

            // Label
            g2.setFont(UIUtil.FONT_BODY);
            g2.setColor(UIUtil.TEXT_PRIMARY);
            g2.drawString(label, 12, y + 16);

            // Bar
            g2.setColor(barColor);
            g2.fillRoundRect(190, y, Math.max(8, w), barHeight, 8, 8);

            // Value text
            g2.setColor(UIUtil.TEXT_SECONDARY);
            g2.setFont(UIUtil.FONT_SMALL_BOLD);
            g2.drawString(String.valueOf(val), 198 + Math.max(8, w), y + 16);

            y += barHeight + 12;
        }
        g2.dispose();
    }

    public void loadAnalyticsData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                demandMap = adminController.getMostWantedBooks();
                typeMap = adminController.getListingsByType();
                condMap = adminController.getListingsByCondition();
                return null;
            }

            @Override
            protected void done() {
                demandChartPanel.repaint();
                typeChartPanel.repaint();
                condChartPanel.repaint();
            }
        };
        worker.execute();
    }
}
