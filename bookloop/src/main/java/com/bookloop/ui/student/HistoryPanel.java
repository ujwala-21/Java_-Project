package com.bookloop.ui.student;

import com.bookloop.controller.ExchangeController;
import com.bookloop.model.ExchangeTransaction;
import com.bookloop.ui.components.ReviewDialog;
import com.bookloop.util.DateUtil;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryPanel extends JPanel {
    private final ExchangeController exchangeController = new ExchangeController();

    private JTable txTable;
    private DefaultTableModel tableModel;
    private List<ExchangeTransaction> transactions = new ArrayList<>();
    private JLabel countLabel;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadHistory();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Transaction & Exchange History 📜");
        countLabel = UIUtil.createMutedLabel("Complete audit record of all your book exchanges, rentals, and donations");
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
        refreshBtn.addActionListener(e -> loadHistory());
        tb.add(refreshBtn);

        JButton reviewBtn = UIUtil.createPrimaryButton("Review Partner ⭐");
        reviewBtn.addActionListener(e -> openReviewDialog());
        tb.add(reviewBtn);

        container.add(tb);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table Card
        JPanel tableCard = UIUtil.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"Tx ID", "Book Title", "Type", "My Role", "Counterparty", "Credits Transferred", "Status", "Date Completed"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        txTable = new JTable(tableModel);
        UIUtil.styleTable(txTable);
        txTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        txTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        txTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        txTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        txTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        txTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        txTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        txTable.getColumnModel().getColumn(7).setPreferredWidth(110);

        JScrollPane sp = UIUtil.createScrollPane(txTable);
        sp.setPreferredSize(new Dimension(850, 420));
        tableCard.add(sp, BorderLayout.CENTER);

        container.add(tableCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    public void loadHistory() {
        SwingWorker<List<ExchangeTransaction>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ExchangeTransaction> doInBackground() throws Exception {
                return exchangeController.getMyTransactions();
            }

            @Override
            protected void done() {
                try {
                    transactions = get();
                    tableModel.setRowCount(0);
                    int currentId = SessionManager.getInstance().getCurrentUserId();

                    for (ExchangeTransaction t : transactions) {
                        boolean isGiver = t.getGiverId() == currentId;
                        String role = isGiver ? "Gave Book" : "Received Book";
                        String partner = isGiver ? t.getReceiverName() : t.getGiverName();

                        tableModel.addRow(new Object[]{
                            t.getId(),
                            t.getBookTitle(),
                            t.getTransactionType().name(),
                            role,
                            partner,
                            t.getCreditsTransferred() + " Credits",
                            t.getStatus().name(),
                            DateUtil.format(t.getCompletedAt() != null ? t.getCompletedAt() : t.getCreatedAt())
                        });
                    }
                    countLabel.setText("You have participated in " + transactions.size() + " completed exchanges");
                } catch (Exception ex) {
                    UIUtil.showError(HistoryPanel.this, "Failed to load transaction history: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void openReviewDialog() {
        int row = txTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a completed transaction from the table to review.");
            return;
        }

        ExchangeTransaction tx = transactions.get(row);
        int currentId = SessionManager.getInstance().getCurrentUserId();
        int partnerId = tx.getGiverId() == currentId ? tx.getReceiverId() : tx.getGiverId();
        String partnerName = tx.getGiverId() == currentId ? tx.getReceiverName() : tx.getGiverName();

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ReviewDialog dlg = new ReviewDialog(parentFrame, partnerId, partnerName, tx.getId(), this::loadHistory);
        dlg.setVisible(true);
    }
}
