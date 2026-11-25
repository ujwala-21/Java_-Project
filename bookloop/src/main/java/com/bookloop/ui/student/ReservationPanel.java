package com.bookloop.ui.student;

import com.bookloop.controller.ExchangeController;
import com.bookloop.model.Reservation;
import com.bookloop.util.DateUtil;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationPanel extends JPanel {
    private final ExchangeController exchangeController = new ExchangeController();
    private final Runnable onExchangeCompleted;

    private JTable resTable;
    private DefaultTableModel tableModel;
    private List<Reservation> reservationList = new ArrayList<>();
    private JLabel countLabel;

    public ReservationPanel(Runnable onExchangeCompleted) {
        this.onExchangeCompleted = onExchangeCompleted;
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadReservations();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Active Campus Book Handover & Reservations 🤝");
        countLabel = UIUtil.createMutedLabel("Both students must confirm physical handover to complete the transaction");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl(), BorderLayout.SOUTH);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 16)));

        // Action Toolbar
        JPanel tb = UIUtil.createCard();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        tb.setAlignmentX(Component.LEFT_ALIGNMENT);
        tb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadReservations());
        tb.add(refreshBtn);

        JButton confirmBtn = UIUtil.createSuccessButton("Confirm Physical Handover ✅");
        confirmBtn.addActionListener(e -> confirmSelectedHandover());
        tb.add(confirmBtn);

        JButton cancelBtn = UIUtil.createDangerButton("Cancel Reservation ❌");
        cancelBtn.addActionListener(e -> cancelSelectedReservation());
        tb.add(cancelBtn);

        container.add(tb);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table Card
        JPanel tableCard = UIUtil.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"ID", "Book Title", "Owner", "Requester", "My Role", "Your Confirmation", "Partner Confirmation", "Expiry Window", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        resTable = new JTable(tableModel);
        UIUtil.styleTable(resTable);
        resTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        resTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        resTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        resTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        resTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        resTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        resTable.getColumnModel().getColumn(6).setPreferredWidth(130);
        resTable.getColumnModel().getColumn(7).setPreferredWidth(110);
        resTable.getColumnModel().getColumn(8).setPreferredWidth(90);

        JScrollPane sp = UIUtil.createScrollPane(resTable);
        sp.setPreferredSize(new Dimension(850, 420));
        tableCard.add(sp, BorderLayout.CENTER);

        container.add(tableCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    private JLabel subLbl() {
        return UIUtil.createMutedLabel("When you meet on campus and exchange the textbook, click 'Confirm Physical Handover'.");
    }

    public void loadReservations() {
        SwingWorker<List<Reservation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Reservation> doInBackground() throws Exception {
                return exchangeController.getMyReservations();
            }

            @Override
            protected void done() {
                try {
                    reservationList = get();
                    tableModel.setRowCount(0);
                    int currentId = SessionManager.getInstance().getCurrentUserId();

                    for (Reservation r : reservationList) {
                        boolean isOwner = r.getOwnerId() == currentId;
                        String role = isOwner ? "Owner (Giving)" : "Requester (Receiving)";

                        String myConf = isOwner
                            ? (r.getOwnerConfirmedAt() != null ? "✅ Confirmed" : "⏳ Pending")
                            : (r.getRequesterConfirmedAt() != null ? "✅ Confirmed" : "⏳ Pending");

                        String theirConf = isOwner
                            ? (r.getRequesterConfirmedAt() != null ? "✅ Confirmed" : "⏳ Pending")
                            : (r.getOwnerConfirmedAt() != null ? "✅ Confirmed" : "⏳ Pending");

                        tableModel.addRow(new Object[]{
                            r.getId(),
                            r.getBookTitle(),
                            r.getOwnerName(),
                            r.getRequesterName(),
                            role,
                            myConf,
                            theirConf,
                            DateUtil.getRemainingDays(r.getExpiresAt()),
                            r.getStatus().name()
                        });
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ReservationPanel.this, "Failed to load reservations: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void confirmSelectedHandover() {
        int row = resTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select an active reservation from the table first.");
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);
        String book = (String) tableModel.getValueAt(row, 1);

        if (!UIUtil.showConfirm(this, "Confirm physical handover of:\n\"" + book + "\"?\n\nOnly confirm if you have met in person and verified the textbook.")) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return exchangeController.confirmPickup(resId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ReservationPanel.this, "Confirmation recorded!\nIf both students confirmed, your transaction is completed and credits awarded.");
                        loadReservations();
                        if (onExchangeCompleted != null) onExchangeCompleted.run();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ReservationPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void cancelSelectedReservation() {
        int row = resTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a reservation to cancel.");
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);

        if (!UIUtil.showConfirm(this, "Are you sure you want to cancel this reservation? The book will return to marketplace availability.")) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return exchangeController.cancelReservation(resId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ReservationPanel.this, "Reservation cancelled.");
                        loadReservations();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ReservationPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
