package com.bookloop.ui.student;

import com.bookloop.controller.ExchangeController;
import com.bookloop.model.ExchangeCycle;
import com.bookloop.model.MatchResult;
import com.bookloop.service.MatchingService;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CircularExchangePanel extends JPanel {
    private final MatchingService matchingService = new MatchingService();
    private final ExchangeController exchangeController = new ExchangeController();

    private JPanel directGrid;
    private JPanel cycleGrid;
    private JLabel directStatusLbl;
    private JLabel cycleStatusLbl;

    public CircularExchangePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        runMatchingAlgorithms();
    }

    private void initComponents() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Smart Direct & Circular Book Exchanges 🔄");
        JLabel subLbl = UIUtil.createMutedLabel("Graph-based algorithmic engine discovering dual matches and multi-student exchange cycles");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        container.add(header, BorderLayout.NORTH);

        // Tabbed View
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtil.FONT_HEADING);

        // Tab 1: Direct Matches
        JPanel directTab = createDirectMatchesTab();
        tabs.addTab("✨ Direct 1-to-1 Matches", directTab);

        // Tab 2: Circular Exchange Loops (Signature Feature)
        JPanel circularTab = createCircularLoopsTab();
        tabs.addTab("🔄 Circular Multi-Student Loops", circularTab);

        container.add(tabs, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    private JPanel createDirectMatchesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UIUtil.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel tb = UIUtil.createCard();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JButton scanBtn = UIUtil.createPrimaryButton("Scan For Direct Matches 🔍");
        scanBtn.addActionListener(e -> loadDirectMatches());
        tb.add(scanBtn);

        directStatusLbl = UIUtil.createMutedLabel("Identifies students who have a book you want and want a book you have");
        tb.add(directStatusLbl);

        panel.add(tb, BorderLayout.NORTH);

        directGrid = new JPanel();
        directGrid.setLayout(new BoxLayout(directGrid, BoxLayout.Y_AXIS));
        directGrid.setBackground(UIUtil.BG_PRIMARY);

        JScrollPane sp = UIUtil.createScrollPane(directGrid);
        sp.setBorder(null);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCircularLoopsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UIUtil.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel tb = UIUtil.createCard();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JButton scanBtn = UIUtil.createPrimaryButton("Run DFS Cycle Detection 🌐");
        scanBtn.addActionListener(e -> loadCircularCycles());
        tb.add(scanBtn);

        cycleStatusLbl = UIUtil.createMutedLabel("Discovers 3-party and 4-party circular loops: A gives to B, B gives to C, C gives back to A!");
        tb.add(cycleStatusLbl);

        panel.add(tb, BorderLayout.NORTH);

        cycleGrid = new JPanel();
        cycleGrid.setLayout(new BoxLayout(cycleGrid, BoxLayout.Y_AXIS));
        cycleGrid.setBackground(UIUtil.BG_PRIMARY);

        JScrollPane sp = UIUtil.createScrollPane(cycleGrid);
        sp.setBorder(null);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    public void runMatchingAlgorithms() {
        loadDirectMatches();
        loadCircularCycles();
    }

    private void loadDirectMatches() {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        directStatusLbl.setText("Scanning database...");

        SwingWorker<List<MatchResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MatchResult> doInBackground() throws Exception {
                return matchingService.getDirectMatches(studentId);
            }

            @Override
            protected void done() {
                try {
                    List<MatchResult> matches = get();
                    displayDirectMatches(matches);
                } catch (Exception ex) {
                    directStatusLbl.setText("Scan failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayDirectMatches(List<MatchResult> matches) {
        directGrid.removeAll();
        directStatusLbl.setText("Found " + matches.size() + " direct mutual matches");

        if (matches.isEmpty()) {
            JPanel empty = UIUtil.createCard();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

            JLabel icon = new JLabel("🤝");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel msg = UIUtil.createHeadingLabel("No direct mutual matches right now");
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel tip = UIUtil.createMutedLabel("List more books for Exchange and add desired textbooks to your Wishlist to trigger matches!");
            tip.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(icon);
            empty.add(Box.createRigidArea(new Dimension(0, 8)));
            empty.add(msg);
            empty.add(Box.createRigidArea(new Dimension(0, 4)));
            empty.add(tip);
            directGrid.add(empty);
        } else {
            for (MatchResult m : matches) {
                JPanel card = UIUtil.createCard();
                card.setLayout(new BorderLayout(16, 8));
                card.setMaximumSize(new Dimension(860, 140));

                JLabel title = UIUtil.createHeadingLabel("✨ Perfect Match with " + m.getOtherStudentName() + " (" + (int) m.getMatchScore() + "% Match Score)");
                title.setForeground(UIUtil.ACCENT_CORAL);
                card.add(title, BorderLayout.NORTH);

                JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
                body.setOpaque(false);

                String iGive = m.getIOffer() != null && m.getIOffer().getBook() != null ? m.getIOffer().getBook().getTitle() : "Your listed book";
                String theyGive = m.getTheyOffer() != null && m.getTheyOffer().getBook() != null ? m.getTheyOffer().getBook().getTitle() : "Their book";

                JLabel giveLbl = UIUtil.createBodyLabel("<b>You Give:</b> " + iGive + " 📤");
                JLabel receiveLbl = UIUtil.createBodyLabel("<b>You Receive:</b> " + theyGive + " 📥");

                body.add(giveLbl);
                body.add(receiveLbl);
                card.add(body, BorderLayout.CENTER);

                JButton proposeBtn = UIUtil.createPrimaryButton("Propose Direct Exchange 🤝");
                proposeBtn.addActionListener(e -> {
                    try {
                        exchangeController.sendRequest(
                            m.getTheyOffer().getId(),
                            m.getIOffer().getId(),
                            "Hey! System found our direct exchange match. Let's trade!",
                            "EXCHANGE"
                        );
                        UIUtil.showSuccess(CircularExchangePanel.this, "Direct exchange proposed to " + m.getOtherStudentName() + "!");
                    } catch (Exception ex) {
                        UIUtil.showError(CircularExchangePanel.this, ex.getMessage());
                    }
                });
                card.add(proposeBtn, BorderLayout.EAST);

                directGrid.add(card);
                directGrid.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        directGrid.revalidate();
        directGrid.repaint();
    }

    private void loadCircularCycles() {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        cycleStatusLbl.setText("Running DFS traversal on student exchange graph...");

        SwingWorker<List<ExchangeCycle>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ExchangeCycle> doInBackground() throws Exception {
                return matchingService.getCircularMatches(studentId);
            }

            @Override
            protected void done() {
                try {
                    List<ExchangeCycle> cycles = get();
                    displayCircularCycles(cycles);
                } catch (Exception ex) {
                    cycleStatusLbl.setText("DFS failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayCircularCycles(List<ExchangeCycle> cycles) {
        cycleGrid.removeAll();
        cycleStatusLbl.setText("Detected " + cycles.size() + " active circular book loops in graph");

        if (cycles.isEmpty()) {
            JPanel empty = UIUtil.createCard();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

            JLabel icon = new JLabel("🌐");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel msg = UIUtil.createHeadingLabel("No circular loops found currently");
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel tip = UIUtil.createMutedLabel("When multiple students list books and wishlist each other's textbooks, closed loops form automatically!");
            tip.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(icon);
            empty.add(Box.createRigidArea(new Dimension(0, 8)));
            empty.add(msg);
            empty.add(Box.createRigidArea(new Dimension(0, 4)));
            empty.add(tip);
            cycleGrid.add(empty);
        } else {
            for (ExchangeCycle c : cycles) {
                JPanel card = UIUtil.createCard();
                card.setLayout(new BorderLayout(16, 12));
                card.setMaximumSize(new Dimension(860, 220));

                JPanel top = new JPanel(new BorderLayout());
                top.setOpaque(false);
                JLabel title = UIUtil.createHeadingLabel("🔄 " + c.getSteps().size() + "-Person Exchange Circle");
                title.setForeground(UIUtil.ACCENT_CORAL);
                JLabel badge = UIUtil.createBadge("Cycle Length: " + c.getSteps().size(), new Color(0xEDE9FE), new Color(0x5B21B6));
                top.add(title, BorderLayout.WEST);
                top.add(badge, BorderLayout.EAST);
                card.add(top, BorderLayout.NORTH);

                // Steps representation
                JPanel stepsPanel = new JPanel();
                stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
                stepsPanel.setOpaque(false);

                for (int i = 0; i < c.getSteps().size(); i++) {
                    ExchangeCycle.Step s = c.getSteps().get(i);
                    String stepText = (i + 1) + ". " + s.getGiverName() + " ➔ passes \"" + s.getBookTitle() + "\" ➔ to " + s.getReceiverName();
                    JLabel sLbl = UIUtil.createBodyLabel(stepText);
                    stepsPanel.add(sLbl);
                    stepsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                }

                card.add(stepsPanel, BorderLayout.CENTER);

                JButton initBtn = UIUtil.createPrimaryButton("Initiate This Exchange Circle 🚀");
                initBtn.addActionListener(e -> {
                    try {
                        int initiatorId = SessionManager.getInstance().getCurrentUserId();
                        matchingService.initiateCircularExchange(c, initiatorId);
                        UIUtil.showSuccess(CircularExchangePanel.this, "Circle initiated! All participants have been notified.");
                    } catch (Exception ex) {
                        UIUtil.showError(CircularExchangePanel.this, ex.getMessage());
                    }
                });
                card.add(initBtn, BorderLayout.SOUTH);

                cycleGrid.add(card);
                cycleGrid.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }
        cycleGrid.revalidate();
        cycleGrid.repaint();
    }
}
