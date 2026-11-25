package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.model.BookListing;
import com.bookloop.util.DateUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MyListingsPanel extends JPanel {
    private final BookController bookController = new BookController();

    private JTable listingsTable;
    private DefaultTableModel tableModel;
    private List<BookListing> myListings = new ArrayList<>();
    private JLabel countLabel;

    public MyListingsPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadMyListings();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("My Book Listings 📋");
        countLabel = UIUtil.createMutedLabel("Manage your active, reserved, and completed listings");
        header.add(titleLbl, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 6)));

        // Hint
        JLabel hintLbl = UIUtil.createMutedLabel("💡 Double-click a listing to edit its details");
        hintLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(hintLbl);
        container.add(Box.createRigidArea(new Dimension(0, 12)));

        // Action Toolbar Card
        JPanel toolbarCard = UIUtil.createCard();
        toolbarCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        toolbarCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadMyListings());
        toolbarCard.add(refreshBtn);

        JButton markSoldBtn = UIUtil.createSecondaryButton("Mark as Sold/Done");
        markSoldBtn.addActionListener(e -> changeSelectedStatus("SOLD"));
        toolbarCard.add(markSoldBtn);

        JButton markAvailableBtn = UIUtil.createSecondaryButton("Mark Available");
        markAvailableBtn.addActionListener(e -> changeSelectedStatus("AVAILABLE"));
        toolbarCard.add(markAvailableBtn);

        JButton deleteBtn = UIUtil.createDangerButton("Delete Listing 🗑️");
        deleteBtn.addActionListener(e -> deleteSelectedListing());
        toolbarCard.add(deleteBtn);

        container.add(toolbarCard);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table Card
        JPanel tableCard = UIUtil.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"ID", "Book Title", "Author", "Type", "Condition", "Price / Credits", "Status", "Date Listed"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        listingsTable = new JTable(tableModel);
        UIUtil.styleTable(listingsTable);
        listingsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        listingsTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        listingsTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        listingsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        listingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        listingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        listingsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        listingsTable.getColumnModel().getColumn(7).setPreferredWidth(110);

        // Double-click to edit
        listingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = listingsTable.getSelectedRow();
                    if (row >= 0 && row < myListings.size()) {
                        openEditDialog(myListings.get(row));
                    }
                }
            }
        });

        JScrollPane sp = UIUtil.createScrollPane(listingsTable);
        sp.setPreferredSize(new Dimension(850, 420));
        tableCard.add(sp, BorderLayout.CENTER);

        container.add(tableCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    private void openEditDialog(BookListing listing) {
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = ancestor instanceof JFrame ? (JFrame) ancestor : null;
        EditListingDialog dialog = new EditListingDialog(parentFrame, listing);
        dialog.setVisible(true);
        if (dialog.wasUpdated()) {
            loadMyListings();
        }
    }

    public void loadMyListings() {
        SwingWorker<List<BookListing>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BookListing> doInBackground() throws Exception {
                return bookController.getMyListings();
            }

            @Override
            protected void done() {
                try {
                    myListings = get();
                    tableModel.setRowCount(0);
                    for (BookListing l : myListings) {
                        String title = l.getBook() != null ? l.getBook().getTitle() : "N/A";
                        String author = l.getBook() != null ? l.getBook().getAuthor() : "N/A";
                        String val = switch (l.getListingType()) {
                            case SELL -> "₹" + l.getAskingPrice();
                            case EXCHANGE -> l.getCreditValue() + " Credits";
                            case RENT -> "₹" + l.getRentalCredits() + " (" + l.getRentalDurationDays() + "d)";
                            case DONATE -> "Free";
                        };

                        tableModel.addRow(new Object[]{
                            l.getId(),
                            title,
                            author,
                            l.getListingType().name(),
                            l.getConditionType().name(),
                            val,
                            l.getStatus().name(),
                            DateUtil.format(l.getCreatedAt())
                        });
                    }
                    countLabel.setText("You have " + myListings.size() + " listings in total");
                } catch (Exception ex) {
                    UIUtil.showError(MyListingsPanel.this, "Failed to load listings: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void changeSelectedStatus(String newStatus) {
        int row = listingsTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a listing from the table first.");
            return;
        }

        int listingId = (int) tableModel.getValueAt(row, 0);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return bookController.updateListingStatus(listingId, newStatus);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(MyListingsPanel.this, "Status updated to " + newStatus);
                        loadMyListings();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(MyListingsPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void deleteSelectedListing() {
        int row = listingsTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a listing from the table first.");
            return;
        }

        int listingId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        if (!UIUtil.showConfirm(this, "Are you sure you want to delete listing for:\n\"" + title + "\"?")) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return bookController.deleteListing(listingId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(MyListingsPanel.this, "Listing removed.");
                        loadMyListings();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(MyListingsPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
