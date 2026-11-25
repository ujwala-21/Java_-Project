package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.model.BookListing;
import com.bookloop.model.Department;
import com.bookloop.ui.components.BookCard;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MarketplacePanel extends JPanel {
    private final BookController bookController = new BookController();
    private final Consumer<BookListing> onSelectListing;

    private JTextField searchField;
    private JComboBox<String> deptFilter;
    private JComboBox<String> typeFilter;
    private JComboBox<String> conditionFilter;
    private JComboBox<String> sortFilter;
    private JPanel gridPanel;
    private JLabel countLabel;
    private List<Department> departmentList = new ArrayList<>();

    public MarketplacePanel(Consumer<BookListing> onSelectListing) {
        this.onSelectListing = onSelectListing;
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadDepartments();
        loadMarketplaceListings();
    }

    private void initComponents() {
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UIUtil.BG_PRIMARY);
        topContainer.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        // Header Title
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel pageTitle = UIUtil.createTitleLabel("Campus Book Marketplace 📚");
        countLabel = UIUtil.createMutedLabel("Discover academic textbooks from fellow students");
        titleBar.add(pageTitle, BorderLayout.WEST);
        titleBar.add(countLabel, BorderLayout.EAST);
        topContainer.add(titleBar);
        topContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // Search and Filters Bar Card
        JPanel filterCard = UIUtil.createCard();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        searchField = UIUtil.createTextField("Search by Title, Author, Subject or ISBN...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { loadMarketplaceListings(); }
            @Override
            public void removeUpdate(DocumentEvent e) { loadMarketplaceListings(); }
            @Override
            public void changedUpdate(DocumentEvent e) { loadMarketplaceListings(); }
        });
        filterCard.add(searchField);

        typeFilter = UIUtil.createComboBox(new String[]{"All Types", "SELL", "EXCHANGE", "RENT", "DONATE"});
        typeFilter.setPreferredSize(new Dimension(120, 36));
        typeFilter.addActionListener(e -> loadMarketplaceListings());
        filterCard.add(typeFilter);

        conditionFilter = UIUtil.createComboBox(new String[]{"All Conditions", "NEW", "EXCELLENT", "GOOD", "ACCEPTABLE"});
        conditionFilter.setPreferredSize(new Dimension(135, 36));
        conditionFilter.addActionListener(e -> loadMarketplaceListings());
        filterCard.add(conditionFilter);

        deptFilter = UIUtil.createComboBox(new String[]{"All Departments"});
        deptFilter.setPreferredSize(new Dimension(160, 36));
        deptFilter.addActionListener(e -> loadMarketplaceListings());
        filterCard.add(deptFilter);

        sortFilter = UIUtil.createComboBox(new String[]{"Newest First", "Price: Low to High", "Price: High to Low"});
        sortFilter.setPreferredSize(new Dimension(145, 36));
        sortFilter.addActionListener(e -> loadMarketplaceListings());
        filterCard.add(sortFilter);

        JButton resetBtn = UIUtil.createGhostButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            typeFilter.setSelectedIndex(0);
            conditionFilter.setSelectedIndex(0);
            deptFilter.setSelectedIndex(0);
            sortFilter.setSelectedIndex(0);
            loadMarketplaceListings();
        });
        filterCard.add(resetBtn);

        topContainer.add(filterCard);
        add(topContainer, BorderLayout.NORTH);

        // Center Scrollable Book Grid
        gridPanel = new JPanel(new GridLayout(0, 4, 16, 16));
        gridPanel.setBackground(UIUtil.BG_PRIMARY);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(UIUtil.BG_PRIMARY);
        wrapperPanel.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = UIUtil.createScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDepartments() {
        SwingWorker<List<Department>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Department> doInBackground() throws Exception {
                return bookController.getDepartments();
            }

            @Override
            protected void done() {
                try {
                    departmentList = get();
                    for (Department d : departmentList) {
                        deptFilter.addItem(d.getName());
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    public void loadMarketplaceListings() {
        String query = searchField.getText().trim();
        if (query.equals("Search by Title, Author, Subject or ISBN...")) query = "";

        String typeStr = (String) typeFilter.getSelectedItem();
        if ("All Types".equals(typeStr)) typeStr = null;

        String condStr = (String) conditionFilter.getSelectedItem();
        if ("All Conditions".equals(condStr)) condStr = null;

        Integer deptId = null;
        int deptIdx = deptFilter.getSelectedIndex();
        if (deptIdx > 0 && deptIdx - 1 < departmentList.size()) {
            deptId = departmentList.get(deptIdx - 1).getId();
        }

        String sortChoice = (String) sortFilter.getSelectedItem();
        String sortBy = "NEWEST";
        if ("Price: Low to High".equals(sortChoice)) sortBy = "PRICE_LOW";
        else if ("Price: High to Low".equals(sortChoice)) sortBy = "PRICE_HIGH";

        final String fQuery = query;
        final String fType = typeStr;
        final String fCond = condStr;
        final Integer fDept = deptId;
        final String fSort = sortBy;

        SwingWorker<List<BookListing>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BookListing> doInBackground() throws Exception {
                return bookController.searchMarketplace(fQuery, fType, fCond, fDept, null, fSort);
            }

            @Override
            protected void done() {
                try {
                    List<BookListing> listings = get();
                    displayListings(listings);
                } catch (Exception ex) {
                    gridPanel.removeAll();
                    gridPanel.add(new JLabel("Failed to load listings: " + ex.getMessage()));
                    gridPanel.revalidate();
                    gridPanel.repaint();
                }
            }
        };
        worker.execute();
    }

    private void displayListings(List<BookListing> listings) {
        gridPanel.removeAll();
        countLabel.setText("Showing " + listings.size() + " available books");

        if (listings.isEmpty()) {
            gridPanel.setLayout(new BorderLayout());
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));

            JLabel icon = new JLabel("🔍");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel msg = UIUtil.createHeadingLabel("No matching books found");
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sub = UIUtil.createMutedLabel("Try adjusting your search keywords or resetting filters");
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(icon);
            empty.add(Box.createRigidArea(new Dimension(0, 10)));
            empty.add(msg);
            empty.add(Box.createRigidArea(new Dimension(0, 6)));
            empty.add(sub);

            gridPanel.add(empty, BorderLayout.CENTER);
        } else {
            gridPanel.setLayout(new GridLayout(0, 4, 16, 16));
            for (BookListing l : listings) {
                BookCard card = new BookCard(l,
                    onSelectListing,
                    listing -> {
                        try {
                            bookController.addToWishlist(listing.getBookId());
                            UIUtil.showSuccess(MarketplacePanel.this, "Added \"" + listing.getBook().getTitle() + "\" to wishlist!");
                        } catch (Exception ex) {
                            UIUtil.showError(MarketplacePanel.this, ex.getMessage());
                        }
                    }
                );
                gridPanel.add(card);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
