package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.model.BookListing;
import com.bookloop.model.WishlistItem;
import com.bookloop.util.DateUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class WishlistPanel extends JPanel {
    private final BookController bookController = new BookController();
    private final Runnable onBrowseMarketplace;
    private final Consumer<BookListing> onViewBookDetails;

    private JTable wishlistTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    public WishlistPanel(Runnable onBrowseMarketplace) {
        this(onBrowseMarketplace, null);
    }

    public WishlistPanel(Runnable onBrowseMarketplace, Consumer<BookListing> onViewBookDetails) {
        this.onBrowseMarketplace = onBrowseMarketplace;
        this.onViewBookDetails = onViewBookDetails;
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadWishlist();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("My Book Wishlist ❤️");
        countLabel = UIUtil.createMutedLabel("Track textbooks you need and monitor live campus availability");
        header.add(titleLbl, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 6)));

        // Hint
        JLabel hintLbl = UIUtil.createMutedLabel("💡 Double-click a book to view its details and request it");
        hintLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(hintLbl);
        container.add(Box.createRigidArea(new Dimension(0, 12)));

        // Action Toolbar
        JPanel toolbarCard = UIUtil.createCard();
        toolbarCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        toolbarCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton refreshBtn = UIUtil.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadWishlist());
        toolbarCard.add(refreshBtn);

        JButton browseBtn = UIUtil.createPrimaryButton("Browse Marketplace 📚");
        browseBtn.addActionListener(e -> {
            if (onBrowseMarketplace != null) onBrowseMarketplace.run();
        });
        toolbarCard.add(browseBtn);

        JButton removeBtn = UIUtil.createDangerButton("Remove from Wishlist 💔");
        removeBtn.addActionListener(e -> removeSelected());
        toolbarCard.add(removeBtn);

        container.add(toolbarCard);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table Card
        JPanel tableCard = UIUtil.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"ID", "Book ID", "Title", "Author", "Subject", "Department", "Live Availability", "Date Saved"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        wishlistTable = new JTable(tableModel);
        UIUtil.styleTable(wishlistTable);
        wishlistTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        wishlistTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        wishlistTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        wishlistTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        wishlistTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        wishlistTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        wishlistTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        wishlistTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Double-click to view book details
        wishlistTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = wishlistTable.getSelectedRow();
                    if (row >= 0) {
                        int bookId = (int) tableModel.getValueAt(row, 1);
                        navigateToBookDetails(bookId);
                    }
                }
            }
        });

        JScrollPane sp = UIUtil.createScrollPane(wishlistTable);
        sp.setPreferredSize(new Dimension(850, 420));
        tableCard.add(sp, BorderLayout.CENTER);

        container.add(tableCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    private void navigateToBookDetails(int bookId) {
        if (onViewBookDetails == null) {
            UIUtil.showError(this, "Book details navigation is not available.");
            return;
        }

        SwingWorker<BookListing, Void> worker = new SwingWorker<>() {
            @Override
            protected BookListing doInBackground() throws Exception {
                return bookController.getListingByBookId(bookId);
            }

            @Override
            protected void done() {
                try {
                    BookListing listing = get();
                    if (listing != null) {
                        onViewBookDetails.accept(listing);
                    } else {
                        UIUtil.showWarning(WishlistPanel.this, "No listing found for this book. It may have been removed.");
                    }
                } catch (Exception ex) {
                    UIUtil.showError(WishlistPanel.this, "Could not load book details: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void loadWishlist() {
        SwingWorker<List<WishlistItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<WishlistItem> doInBackground() throws Exception {
                return bookController.getMyWishlist();
            }

            @Override
            protected void done() {
                try {
                    List<WishlistItem> items = get();
                    tableModel.setRowCount(0);
                    for (WishlistItem w : items) {
                        String avail = w.getAvailableListingsCount() > 0
                            ? "✅ " + w.getAvailableListingsCount() + " available"
                            : "❌ Out of stock";

                        tableModel.addRow(new Object[]{
                            w.getId(),
                            w.getBookId(),
                            w.getBookTitle(),
                            w.getAuthor(),
                            w.getSubject() != null ? w.getSubject() : "General",
                            w.getDepartmentName() != null ? w.getDepartmentName() : "General",
                            avail,
                            DateUtil.format(w.getCreatedAt())
                        });
                    }
                    countLabel.setText("You have " + items.size() + " books in your wishlist");
                } catch (Exception ex) {
                    UIUtil.showError(WishlistPanel.this, "Failed to load wishlist: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void removeSelected() {
        int row = wishlistTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select an item from the table first.");
            return;
        }

        int bookId = (int) tableModel.getValueAt(row, 1);
        String title = (String) tableModel.getValueAt(row, 2);

        if (!UIUtil.showConfirm(this, "Remove \"" + title + "\" from your wishlist?")) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return bookController.removeFromWishlist(bookId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(WishlistPanel.this, "Removed from wishlist.");
                        loadWishlist();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(WishlistPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
