package com.bookloop.ui.student;

import com.bookloop.controller.WalletController;
import com.bookloop.model.CreditTransaction;
import com.bookloop.util.DateUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class WalletPanel extends JPanel {
    private final WalletController walletController = new WalletController();

    private JLabel balanceLabel;
    private JButton claimBonusBtn;
    private JTable txTable;
    private DefaultTableModel tableModel;

    public WalletPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadWalletData();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("My BookLoop Credit Wallet 💳");
        JLabel subLbl = UIUtil.createMutedLabel("Internal exchange currency earned by passing books forward to fellow students");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 18)));

        // Wallet Balance Hero Card (Peach to Coral gradient)
        JPanel balanceHero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIUtil.ACCENT_PEACH, getWidth(), getHeight(), UIUtil.ACCENT_CORAL);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        balanceHero.setLayout(new BorderLayout(16, 8));
        balanceHero.setOpaque(false);
        balanceHero.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        balanceHero.setMaximumSize(new Dimension(860, 130));
        balanceHero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftHero = new JPanel();
        leftHero.setLayout(new BoxLayout(leftHero, BoxLayout.Y_AXIS));
        leftHero.setOpaque(false);

        JLabel curTitle = new JLabel("AVAILABLE EXCHANGE CREDITS");
        curTitle.setFont(UIUtil.FONT_SMALL_BOLD);
        curTitle.setForeground(new Color(0xFFF0EB));

        balanceLabel = new JLabel("0.00 Credits");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        balanceLabel.setForeground(Color.WHITE);

        JLabel info = new JLabel("1 Credit = ₹1.00 Textbook Exchange Equivalent");
        info.setFont(UIUtil.FONT_SMALL);
        info.setForeground(new Color(0xFFE2D8));

        leftHero.add(curTitle);
        leftHero.add(Box.createRigidArea(new Dimension(0, 4)));
        leftHero.add(balanceLabel);
        leftHero.add(Box.createRigidArea(new Dimension(0, 4)));
        leftHero.add(info);
        balanceHero.add(leftHero, BorderLayout.WEST);

        // Quick deposit demo action
        JPanel rightHero = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 16));
        rightHero.setOpaque(false);
        claimBonusBtn = new JButton("Claim Daily Campus Bonus (+25) 🎁");
        claimBonusBtn.setFont(UIUtil.FONT_BUTTON);
        claimBonusBtn.setBackground(Color.WHITE);
        claimBonusBtn.setForeground(UIUtil.ACCENT_CORAL);
        claimBonusBtn.setFocusPainted(false);
        claimBonusBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        claimBonusBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        claimBonusBtn.addActionListener(e -> claimCampusCredits());
        rightHero.add(claimBonusBtn);
        balanceHero.add(rightHero, BorderLayout.EAST);

        container.add(balanceHero);
        container.add(Box.createRigidArea(new Dimension(0, 18)));

        // History Card
        JPanel historyCard = UIUtil.createCard();
        historyCard.setLayout(new BorderLayout());
        historyCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel histHeader = new JPanel(new BorderLayout());
        histHeader.setOpaque(false);
        histHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        histHeader.add(UIUtil.createHeadingLabel("Credit Transaction Audit Trail"), BorderLayout.WEST);
        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadWalletData());
        histHeader.add(refreshBtn, BorderLayout.EAST);
        historyCard.add(histHeader, BorderLayout.NORTH);

        String[] cols = {"Tx ID", "Type", "Amount", "Reason / Description", "Balance After", "Timestamp"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        txTable = new JTable(tableModel);
        UIUtil.styleTable(txTable);
        txTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        txTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        txTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        txTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        txTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        txTable.getColumnModel().getColumn(5).setPreferredWidth(140);

        JScrollPane sp = UIUtil.createScrollPane(txTable);
        sp.setPreferredSize(new Dimension(820, 360));
        historyCard.add(sp, BorderLayout.CENTER);

        container.add(historyCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    public void loadWalletData() {
        SwingWorker<BigDecimal, Void> worker = new SwingWorker<>() {
            private List<CreditTransaction> history;
            private boolean alreadyClaimedToday;

            @Override
            protected BigDecimal doInBackground() throws Exception {
                BigDecimal bal = walletController.getMyBalance();
                history = walletController.getMyTransactions();
                alreadyClaimedToday = walletController.hasClaimedDailyBonusToday();
                return bal;
            }

            @Override
            protected void done() {
                try {
                    BigDecimal balance = get();
                    balanceLabel.setText(balance + " Credits");

                    if (alreadyClaimedToday) {
                        claimBonusBtn.setText("Daily Bonus Claimed Today ✅");
                        claimBonusBtn.setEnabled(false);
                        claimBonusBtn.setBackground(new Color(0xE5E7EB));
                        claimBonusBtn.setForeground(new Color(0x6B7280));
                        claimBonusBtn.setCursor(Cursor.getDefaultCursor());
                        claimBonusBtn.setToolTipText("You have already claimed today's bonus. Come back tomorrow!");
                    } else {
                        claimBonusBtn.setText("Claim Daily Campus Bonus (+25) 🎁");
                        claimBonusBtn.setEnabled(true);
                        claimBonusBtn.setBackground(Color.WHITE);
                        claimBonusBtn.setForeground(UIUtil.ACCENT_CORAL);
                        claimBonusBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        claimBonusBtn.setToolTipText("Claim 25 free credits once per day!");
                    }

                    tableModel.setRowCount(0);
                    for (CreditTransaction ct : history) {
                        String sign = ct.getTransactionType() == CreditTransaction.TransactionType.CREDIT ? "+ " : "- ";
                        tableModel.addRow(new Object[]{
                            ct.getId(),
                            ct.getTransactionType().name(),
                            sign + ct.getAmount(),
                            ct.getReason(),
                            ct.getBalanceAfter(),
                            DateUtil.formatWithTime(ct.getCreatedAt())
                        });
                    }
                } catch (Exception ex) {
                    UIUtil.showError(WalletPanel.this, "Failed to load wallet balance: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void claimCampusCredits() {
        claimBonusBtn.setEnabled(false);
        claimBonusBtn.setText("Claiming...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return walletController.claimDailyBonus();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(WalletPanel.this, "25 Credits added to your wallet! 🎉\n(Come back tomorrow for your next daily bonus!)");
                        loadWalletData();
                    }
                } catch (Exception ex) {
                    UIUtil.showWarning(WalletPanel.this, ex.getMessage());
                    loadWalletData();
                }
            }
        };
        worker.execute();
    }
}
