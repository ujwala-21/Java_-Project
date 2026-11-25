package com.bookloop.ui.admin;

import com.bookloop.controller.AdminController;
import com.bookloop.model.BookListing;
import com.bookloop.util.DateUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ListingManagementPanel extends JPanel {
    private final AdminController adminController = new AdminController();

    private JTable listingsTable;
    private DefaultTableModel tableModel;
    private List<BookListing> allListings = new ArrayList<>();
    private JLabel countLabel;

    public ListingManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadAllListings();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Marketplace Listings Moderation 📋");
        countLabel = UIUtil.createMutedLabel("Oversee textbook postings, maintain academic integrity, and remove inappropriate entries");
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
        refreshBtn.addActionListener(e -> loadAllListings());
        tb.add(refreshBtn);

        JButton removeBtn = UIUtil.createDangerButton("Remove Inappropriate Listing ⚠️");
        removeBtn.addActionListener(e -> removeSelectedListing());
        tb.add(removeBtn);

        container.add(tb);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table Card
        JPanel tableCard = UIUtil.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"ID", "Book Title", "Author", "Owner Student", "Type", "Condition", "Price / Credits", "Status", "Date Posted"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        listingsTable = new JTable(tableModel);
        UIUtil.styleTable(listingsTable);
        listingsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        listingsTable.getColumnModel().getColumn(1).setPreferredWidth(210);
        listingsTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        listingsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        listingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        listingsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        listingsTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        listingsTable.getColumnModel().getColumn(7).setPreferredWidth(90);
        listingsTable.getColumnModel().getColumn(8).setPreferredWidth(100);

        JScrollPane sp = UIUtil.createScrollPane(listingsTable);
        sp.setPreferredSize(new Dimension(850, 420));
        tableCard.add(sp, BorderLayout.CENTER);

        container.add(tableCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    public void loadAllListings() {
        SwingWorker<List<BookListing>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BookListing> doInBackground() throws Exception {
                return adminController.getAllListings();
            }

            @Override
            protected void done() {
                try {
                    allListings = get();
                    tableModel.setRowCount(0);
                    for (BookListing l : allListings) {
                        String title = l.getBook() != null ? l.getBook().getTitle() : "N/A";
                        String author = l.getBook() != null ? l.getBook().getAuthor() : "N/A";
                        String val = switch (l.getListingType()) {
                            case SELL -> "₹" + l.getAskingPrice();
                            case EXCHANGE -> l.getCreditValue() + " Credits";
                            case RENT -> "₹" + l.getRentalCredits();
                            case DONATE -> "Free";
                        };

                        tableModel.addRow(new Object[]{
                            l.getId(),
                            title,
                            author,
                            l.getOwnerName(),
                            l.getListingType().name(),
                            l.getConditionType().name(),
                            val,
                            l.getStatus().name(),
                            DateUtil.format(l.getCreatedAt())
                        });
                    }
                    countLabel.setText("Total campus listings: " + allListings.size());
                } catch (Exception ex) {
                    UIUtil.showError(ListingManagementPanel.this, "Failed to load listings: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void removeSelectedListing() {
        int row = listingsTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a listing from the table first.");
            return;
        }

        int listingId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        if (!UIUtil.showConfirm(this, "Moderator Action: Remove/Cancel listing for:\n\"" + title + "\"?")) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return adminController.removeInappropriateListing(listingId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(ListingManagementPanel.this, "Listing removed / cancelled by admin.");
                        loadAllListings();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ListingManagementPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
