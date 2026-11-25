package com.bookloop.ui.components;

import com.bookloop.model.BookListing;
import com.bookloop.util.ImageUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class BookCard extends JPanel {
    private final BookListing listing;
    private final Consumer<BookListing> onClick;
    private final Consumer<BookListing> onWishlist;

    public BookCard(BookListing listing, Consumer<BookListing> onClick, Consumer<BookListing> onWishlist) {
        this.listing = listing;
        this.onClick = onClick;
        this.onWishlist = onWishlist;

        setLayout(new BorderLayout());
        setBackground(UIUtil.CARD_BG);
        setPreferredSize(new Dimension(220, 310));
        setMaximumSize(new Dimension(240, 320));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtil.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        initComponents();
        setupHoverEffects();
    }

    private void initComponents() {
        // TOP BADGES (Listing Type & Condition)
        JPanel topBadgePanel = new JPanel(new BorderLayout());
        topBadgePanel.setOpaque(false);
        JLabel typeBadge = UIUtil.createStatusBadge(listing.getListingType().name());
        JLabel condBadge = UIUtil.createStatusBadge(listing.getConditionType().name());
        topBadgePanel.add(typeBadge, BorderLayout.WEST);
        topBadgePanel.add(condBadge, BorderLayout.EAST);
        add(topBadgePanel, BorderLayout.NORTH);

        // CENTER (Image & Book Details)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Cover thumbnail
        JLabel imageLabel = new JLabel(ImageUtil.loadAndScaleImage(listing.getImagePath(), listing.getBookId(), 180, 120));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(imageLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Title
        String titleStr = listing.getBook() != null ? listing.getBook().getTitle() : "Book Title";
        if (titleStr.length() > 34) {
            titleStr = titleStr.substring(0, 31) + "...";
        }
        JLabel titleLbl = new JLabel("<html><b>" + titleStr + "</b></html>");
        titleLbl.setFont(UIUtil.FONT_BODY_BOLD);
        titleLbl.setForeground(UIUtil.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(titleLbl);

        // Author
        String authorStr = listing.getBook() != null && listing.getBook().getAuthor() != null ? listing.getBook().getAuthor() : "Unknown Author";
        if (authorStr.length() > 25) authorStr = authorStr.substring(0, 22) + "...";
        JLabel authorLbl = new JLabel(authorStr);
        authorLbl.setFont(UIUtil.FONT_SMALL);
        authorLbl.setForeground(UIUtil.TEXT_SECONDARY);
        authorLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(authorLbl);

        add(centerPanel, BorderLayout.CENTER);

        // BOTTOM BAR (Price/Credits & Wishlist Action)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        String valueText = switch (listing.getListingType()) {
            case SELL -> "₹" + (listing.getAskingPrice() != null ? listing.getAskingPrice() : "0");
            case EXCHANGE -> (listing.getCreditValue() != null ? listing.getCreditValue() : "50") + " Credits";
            case RENT -> "₹" + (listing.getRentalCredits() != null ? listing.getRentalCredits() : "20") + " / " + (listing.getRentalDurationDays() != null ? listing.getRentalDurationDays() : 7) + "d";
            case DONATE -> "FREE 🎁";
        };

        JLabel valueLbl = new JLabel(valueText);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueLbl.setForeground(UIUtil.ACCENT_CORAL);
        bottomPanel.add(valueLbl, BorderLayout.WEST);

        JButton wishBtn = new JButton("❤️");
        wishBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        wishBtn.setToolTipText("Add to Wishlist");
        wishBtn.setFocusPainted(false);
        wishBtn.setBorderPainted(false);
        wishBtn.setContentAreaFilled(false);
        wishBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        wishBtn.addActionListener(e -> {
            if (onWishlist != null) onWishlist.accept(listing);
        });
        bottomPanel.add(wishBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupHoverEffects() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIUtil.ACCENT_PEACH, 2),
                    BorderFactory.createEmptyBorder(11, 11, 11, 11)
                ));
                setBackground(UIUtil.CARD_BG_ALT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIUtil.BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
                setBackground(UIUtil.CARD_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.accept(listing);
            }
        });
    }
}
