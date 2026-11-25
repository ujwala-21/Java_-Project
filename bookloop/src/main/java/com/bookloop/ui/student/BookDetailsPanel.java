package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.controller.ExchangeController;
import com.bookloop.dao.ReviewDAO;
import com.bookloop.model.BookListing;
import com.bookloop.model.Review;
import com.bookloop.util.DateUtil;
import com.bookloop.util.ImageUtil;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookDetailsPanel extends JPanel {
    private final BookListing listing;
    private final Runnable onBack;
    private final BookController bookController = new BookController();
    private final ExchangeController exchangeController = new ExchangeController();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public BookDetailsPanel(BookListing listing, Runnable onBack) {
        this.listing = listing;
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Back Button & Header
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false);
        JButton backBtn = UIUtil.createGhostButton("← Back to Marketplace");
        backBtn.setFont(UIUtil.FONT_BODY_BOLD);
        backBtn.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        topBar.add(backBtn);
        topBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(topBar);
        container.add(Box.createRigidArea(new Dimension(0, 16)));

        // Main Detail Card (Two Columns)
        JPanel mainCard = UIUtil.createCard();
        mainCard.setLayout(new BorderLayout(24, 0));
        mainCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainCard.setMaximumSize(new Dimension(1000, 520));

        // LEFT COLUMN: Book Cover & Quick Badges
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);
        leftCol.setPreferredSize(new Dimension(280, 440));

        JLabel coverImg = new JLabel(ImageUtil.loadAndScaleImage(listing.getImagePath(), listing.getBookId(), 260, 320));
        coverImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftCol.add(coverImg);
        leftCol.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        badges.setOpaque(false);
        badges.add(UIUtil.createStatusBadge(listing.getListingType().name()));
        badges.add(UIUtil.createStatusBadge(listing.getConditionType().name()));
        badges.add(UIUtil.createStatusBadge(listing.getStatus().name()));
        leftCol.add(badges);

        mainCard.add(leftCol, BorderLayout.WEST);

        // RIGHT COLUMN: Book Details & Actions
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        String title = listing.getBook() != null ? listing.getBook().getTitle() : "Book Title";
        JLabel titleLbl = UIUtil.createTitleLabel(title);
        rightCol.add(titleLbl);
        rightCol.add(Box.createRigidArea(new Dimension(0, 4)));

        String author = listing.getBook() != null ? "by " + listing.getBook().getAuthor() : "";
        JLabel authorLbl = UIUtil.createSubtitleLabel(author);
        authorLbl.setFont(UIUtil.FONT_HEADING);
        authorLbl.setForeground(UIUtil.TEXT_SECONDARY);
        rightCol.add(authorLbl);
        rightCol.add(Box.createRigidArea(new Dimension(0, 14)));

        // Pricing / Credits callout box
        JPanel priceBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        priceBox.setBackground(UIUtil.BG_SECONDARY);
        priceBox.setBorder(BorderFactory.createLineBorder(UIUtil.BORDER_COLOR, 1));
        priceBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        String priceStr = switch (listing.getListingType()) {
            case SELL -> "Price: ₹" + (listing.getAskingPrice() != null ? listing.getAskingPrice() : "0");
            case EXCHANGE -> "Exchange Credit Value: " + (listing.getCreditValue() != null ? listing.getCreditValue() : "50") + " Credits";
            case RENT -> "Rental: ₹" + (listing.getRentalCredits() != null ? listing.getRentalCredits() : "0") + " for " + (listing.getRentalDurationDays() != null ? listing.getRentalDurationDays() : 7) + " days";
            case DONATE -> "Cost: Free (Donation)";
        };
        JLabel priceLbl = new JLabel(priceStr);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        priceLbl.setForeground(UIUtil.ACCENT_CORAL);
        priceBox.add(priceLbl);
        rightCol.add(priceBox);
        rightCol.add(Box.createRigidArea(new Dimension(0, 14)));

        // Academic details
        String dept = listing.getBook() != null && listing.getBook().getDepartmentName() != null ? listing.getBook().getDepartmentName() : "General";
        String isbn = listing.getBook() != null && listing.getBook().getIsbn() != null ? listing.getBook().getIsbn() : "N/A";
        String edition = listing.getBook() != null && listing.getBook().getEdition() != null ? listing.getBook().getEdition() : "Standard";

        JPanel metaGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        metaGrid.setOpaque(false);
        metaGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        metaGrid.add(createMetaItem("Department", dept));
        metaGrid.add(createMetaItem("ISBN", isbn));
        metaGrid.add(createMetaItem("Edition", edition));
        metaGrid.add(createMetaItem("Listed", DateUtil.format(listing.getCreatedAt())));
        rightCol.add(metaGrid);
        rightCol.add(Box.createRigidArea(new Dimension(0, 16)));

        // Condition Disclosures (Transparency section)
        JLabel condTitle = UIUtil.createHeadingLabel("Condition Disclosure:");
        rightCol.add(condTitle);
        rightCol.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        tagsPanel.setOpaque(false);
        tagsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        if (listing.isHasHighlights()) tagsPanel.add(UIUtil.createBadge("📝 Highlighting", new Color(0xFEF3C7), new Color(0x92400E)));
        if (listing.isHasAnnotations()) tagsPanel.add(UIUtil.createBadge("✏️ Annotations/Notes", new Color(0xFEF3C7), new Color(0x92400E)));
        if (listing.isHasMissingPages()) tagsPanel.add(UIUtil.createBadge("⚠️ Missing Pages", new Color(0xFEE2E2), new Color(0x991B1B)));
        if (listing.isHasDamagedCover()) tagsPanel.add(UIUtil.createBadge("⚡ Damaged Cover", new Color(0xFEE2E2), new Color(0x991B1B)));
        if (listing.isHasTornPages()) tagsPanel.add(UIUtil.createBadge("📄 Torn Pages", new Color(0xFEE2E2), new Color(0x991B1B)));
        if (listing.isHasWaterDamage()) tagsPanel.add(UIUtil.createBadge("💧 Water Damage", new Color(0xFEE2E2), new Color(0x991B1B)));
        if (!listing.isHasHighlights() && !listing.isHasAnnotations() && !listing.isHasMissingPages() && !listing.isHasDamagedCover() && !listing.isHasTornPages() && !listing.isHasWaterDamage()) {
            tagsPanel.add(UIUtil.createBadge("✨ Clean Copy (No Marks)", new Color(0xDEF7EC), new Color(0x03543F)));
        }
        rightCol.add(tagsPanel);
        rightCol.add(Box.createRigidArea(new Dimension(0, 12)));

        // Description
        if (listing.getDescription() != null && !listing.getDescription().isEmpty()) {
            JLabel descLbl = new JLabel("<html><p>" + listing.getDescription() + "</p></html>");
            descLbl.setFont(UIUtil.FONT_BODY);
            descLbl.setForeground(UIUtil.TEXT_SECONDARY);
            rightCol.add(descLbl);
            rightCol.add(Box.createRigidArea(new Dimension(0, 16)));
        }

        // Seller Info
        JPanel sellerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        sellerPanel.setOpaque(false);
        JLabel ownerLbl = UIUtil.createBodyLabel("<html>Owner: <b>" + listing.getOwnerName() + "</b></html>");
        sellerPanel.add(ownerLbl);
        rightCol.add(sellerPanel);
        rightCol.add(Box.createRigidArea(new Dimension(0, 20)));

        // Action Buttons
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        boolean isOwner = listing.getOwnerId() == currentUserId;

        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionsBar.setOpaque(false);

        if (!isOwner) {
            JButton reqBtn = UIUtil.createPrimaryButton("Request Book 🤝");
            reqBtn.addActionListener(e -> promptExchangeRequest());
            actionsBar.add(reqBtn);

            JButton wishBtn = UIUtil.createSecondaryButton("Add to Wishlist ❤️");
            wishBtn.addActionListener(e -> addToWishlist());
            actionsBar.add(wishBtn);
        } else {
            JLabel ownerNotice = UIUtil.createBadge("This is your listing", new Color(0xEDE9FE), new Color(0x5B21B6));
            actionsBar.add(ownerNotice);
        }

        rightCol.add(actionsBar);
        mainCard.add(rightCol, BorderLayout.CENTER);

        container.add(mainCard);

        JScrollPane scrollPane = UIUtil.createScrollPane(container);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMetaItem(String key, String val) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel k = UIUtil.createMutedLabel(key + ":");
        JLabel v = UIUtil.createBodyLabel("<html><b>" + val + "</b></html>");
        p.add(k);
        p.add(v);
        return p;
    }

    private void promptExchangeRequest() {
        String msg = JOptionPane.showInputDialog(this,
            "Enter a message for " + listing.getOwnerName() + ":\n(Include preferred campus meeting spot)",
            "Send Book Request",
            JOptionPane.PLAIN_MESSAGE);

        if (msg != null && !msg.trim().isEmpty()) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    String reqType = switch (listing.getListingType()) {
                        case SELL -> "BUY";
                        case EXCHANGE -> "EXCHANGE";
                        case RENT -> "RENT";
                        case DONATE -> "DONATE";
                    };
                    exchangeController.sendRequest(
                        listing.getId(),
                        null,
                        msg.trim(),
                        reqType
                    );
                    return true;
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            UIUtil.showSuccess(BookDetailsPanel.this, "Request sent to " + listing.getOwnerName() + "!\nTrack it in Exchange Requests tab.");
                        }
                    } catch (Exception ex) {
                        UIUtil.showError(BookDetailsPanel.this, ex.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }

    private void addToWishlist() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return bookController.addToWishlist(listing.getBookId());
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(BookDetailsPanel.this, "Book added to your Wishlist!");
                    }
                } catch (Exception ex) {
                    UIUtil.showError(BookDetailsPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
