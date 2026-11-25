package com.bookloop.ui.student;

import com.bookloop.controller.ExchangeController;
import com.bookloop.model.ExchangeRequest;
import com.bookloop.util.DateUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRequestsPanel extends JPanel {
    private final ExchangeController exchangeController = new ExchangeController();
    private final Runnable onReservationCreated;

    private JTable incomingTable;
    private DefaultTableModel incomingModel;
    private List<ExchangeRequest> incomingList = new ArrayList<>();

    private JTable outgoingTable;
    private DefaultTableModel outgoingModel;
    private List<ExchangeRequest> outgoingList = new ArrayList<>();

    public ExchangeRequestsPanel(Runnable onReservationCreated) {
        this.onReservationCreated = onReservationCreated;
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadAllRequests();
    }

    private void initComponents() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Exchange & Book Requests 🤝");
        JLabel subLbl = UIUtil.createMutedLabel("Manage incoming student offers for your books and track your outgoing requests");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        container.add(header, BorderLayout.NORTH);

        // Tabbed Pane for Incoming vs Outgoing
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtil.FONT_HEADING);

        // Tab 1: Incoming
        JPanel incomingTab = createIncomingTab();
        tabbedPane.addTab("📥 Incoming Requests (For My Books)", incomingTab);

        // Tab 2: Outgoing
        JPanel outgoingTab = createOutgoingTab();
        tabbedPane.addTab("📤 Outgoing Requests (Books I Want)", outgoingTab);

        container.add(tabbedPane, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    private JPanel createIncomingTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UIUtil.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel tb = UIUtil.createCard();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadIncomingRequests());
        tb.add(refreshBtn);

        JButton acceptBtn = UIUtil.createSuccessButton("Accept Request ✅");
        acceptBtn.addActionListener(e -> acceptSelectedRequest());
        tb.add(acceptBtn);

        JButton rejectBtn = UIUtil.createDangerButton("Reject ❌");
        rejectBtn.addActionListener(e -> rejectSelectedRequest());
        tb.add(rejectBtn);

        panel.add(tb, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Student Requester", "Requested Book", "Type", "Offered Book", "Message", "Date", "Status"};
        incomingModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        incomingTable = new JTable(incomingModel);
        UIUtil.styleTable(incomingTable);
        incomingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        incomingTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        incomingTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        incomingTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        incomingTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        incomingTable.getColumnModel().getColumn(5).setPreferredWidth(160);
        incomingTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        incomingTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        panel.add(UIUtil.createScrollPane(incomingTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createOutgoingTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UIUtil.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel tb = UIUtil.createCard();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadOutgoingRequests());
        tb.add(refreshBtn);

        JButton cancelBtn = UIUtil.createDangerButton("Cancel My Request 🚫");
        cancelBtn.addActionListener(e -> cancelSelectedRequest());
        tb.add(cancelBtn);

        panel.add(tb, BorderLayout.NORTH);

        String[] cols = {"ID", "Book Title", "Owner", "Type", "My Offered Book", "Message", "Date", "Status"};
        outgoingModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        outgoingTable = new JTable(outgoingModel);
        UIUtil.styleTable(outgoingTable);
        outgoingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        outgoingTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        outgoingTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        outgoingTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        outgoingTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        outgoingTable.getColumnModel().getColumn(5).setPreferredWidth(160);
        outgoingTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        outgoingTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        panel.add(UIUtil.createScrollPane(outgoingTable), BorderLayout.CENTER);
        return panel;
    }

    public void loadAllRequests() {
        loadIncomingRequests();
        loadOutgoingRequests();
    }

    public void loadIncomingRequests() {
        SwingWorker<List<ExchangeRequest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ExchangeRequest> doInBackground() throws Exception {
                return exchangeController.getIncomingRequests();
            }

            @Override
            protected void done() {
                try {
                    incomingList = get();
                    incomingModel.setRowCount(0);
                    for (ExchangeRequest r : incomingList) {
                        incomingModel.addRow(new Object[]{
                            r.getId(),
                            r.getRequesterName(),
                            r.getListingTitle(),
                            r.getRequestType().name(),
                            r.getOfferedListingTitle() != null ? r.getOfferedListingTitle() : "—",
                            r.getMessage(),
                            DateUtil.format(r.getCreatedAt()),
                            r.getStatus().name()
                        });
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ExchangeRequestsPanel.this, "Failed to load incoming requests: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void loadOutgoingRequests() {
        SwingWorker<List<ExchangeRequest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ExchangeRequest> doInBackground() throws Exception {
                return exchangeController.getOutgoingRequests();
            }

            @Override
            protected void done() {
                try {
                    outgoingList = get();
                    outgoingModel.setRowCount(0);
                    for (ExchangeRequest r : outgoingList) {
                        outgoingModel.addRow(new Object[]{
                            r.getId(),
                            r.getListingTitle(),
                            r.getOwnerName(),
                            r.getRequestType().name(),
                            r.getOfferedListingTitle() != null ? r.getOfferedListingTitle() : "—",
                            r.getMessage(),
                            DateUtil.format(r.getCreatedAt()),
                            r.getStatus().name()
                        });
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ExchangeRequestsPanel.this, "Failed to load outgoing requests: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void acceptSelectedRequest() {
        int row = incomingTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select an incoming request to accept.");
            return;
        }

        int requestId = (int) incomingModel.getValueAt(row, 0);
        String requester = (String) incomingModel.getValueAt(row, 1);
        String book = (String) incomingModel.getValueAt(row, 2);

        if (!UIUtil.showConfirm(this, "Accept request from " + requester + " for:\n\"" + book + "\"?\n\nThis will reserve the book and open a 7-day campus handover window.")) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                exchangeController.acceptRequest(requestId);
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ExchangeRequestsPanel.this, "Request accepted! Book is now RESERVED.\nGo to Reservations tab to confirm handover.");
                        loadAllRequests();
                        if (onReservationCreated != null) onReservationCreated.run();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ExchangeRequestsPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void rejectSelectedRequest() {
        int row = incomingTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select an incoming request to reject.");
            return;
        }

        int requestId = (int) incomingModel.getValueAt(row, 0);

        if (!UIUtil.showConfirm(this, "Reject this exchange offer?")) return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return exchangeController.rejectRequest(requestId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ExchangeRequestsPanel.this, "Request rejected.");
                        loadIncomingRequests();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ExchangeRequestsPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void cancelSelectedRequest() {
        int row = outgoingTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a request to cancel.");
            return;
        }

        int requestId = (int) outgoingModel.getValueAt(row, 0);

        if (!UIUtil.showConfirm(this, "Cancel your pending request?")) return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return exchangeController.cancelRequest(requestId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ExchangeRequestsPanel.this, "Request cancelled.");
                        loadOutgoingRequests();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ExchangeRequestsPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
