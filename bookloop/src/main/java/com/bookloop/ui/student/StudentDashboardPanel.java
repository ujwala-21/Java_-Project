package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.controller.ExchangeController;
import com.bookloop.controller.WalletController;
import com.bookloop.model.BookListing;
import com.bookloop.model.Student;
import com.bookloop.ui.components.BookCard;
import com.bookloop.ui.components.StatCard;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

public class StudentDashboardPanel extends JPanel {
    private final Consumer<String> onNavigate;
    private final Consumer<BookListing> onSelectListing;
    private final BookController bookController = new BookController();
    private final ExchangeController exchangeController = new ExchangeController();
    private final WalletController walletController = new WalletController();

    private JLabel welcomeLabel;
    private JLabel deptLabel;
    private StatCard listingsCard;
    private StatCard requestsCard;
    private StatCard reservationsCard;
    private StatCard walletCard;
    private JPanel recentGrid;

    public StudentDashboardPanel(Consumer<String> onNavigate, Consumer<BookListing> onSelectListing) {
        this.onNavigate = onNavigate;
        this.onSelectListing = onSelectListing;

        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // WELCOME HEADER & CAMPUS BADGE
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel leftTitle = new JPanel();
        leftTitle.setLayout(new BoxLayout(leftTitle, BoxLayout.Y_AXIS));
        leftTitle.setOpaque(false);

        welcomeLabel = UIUtil.createTitleLabel("Welcome to BookLoop! 👋");
        deptLabel = UIUtil.createMutedLabel("Your campus peer-to-peer textbook exchange");
        leftTitle.add(welcomeLabel);
        leftTitle.add(Box.createRigidArea(new Dimension(0, 4)));
        leftTitle.add(deptLabel);
        topHeader.add(leftTitle, BorderLayout.WEST);

        JButton findMatchBtn = UIUtil.createPrimaryButton("Find Book Matches 🔄");
        findMatchBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("CIRCULAR"); });
        topHeader.add(findMatchBtn, BorderLayout.EAST);

        topHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(topHeader);
        container.add(Box.createRigidArea(new Dimension(0, 18)));

        // 4 STAT CARDS
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        listingsCard = new StatCard("📋", "MY ACTIVE LISTINGS", "0", "Books listed", UIUtil.ACCENT_PEACH);
        requestsCard = new StatCard("📩", "PENDING REQUESTS", "0", "Awaiting review", UIUtil.ACCENT_CORAL);
        reservationsCard = new StatCard("🤝", "CAMPUS HANDOVERS", "0", "Active reservations", UIUtil.SUCCESS);
        walletCard = new StatCard("💳", "WALLET BALANCE", "0.00", "Exchange credits", UIUtil.ACCENT_PLUM);

        statsRow.add(listingsCard);
        statsRow.add(requestsCard);
        statsRow.add(reservationsCard);
        statsRow.add(walletCard);

        container.add(statsRow);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // QUICK ACTIONS CARD
        JPanel actionsCard = UIUtil.createCard();
        actionsCard.setLayout(new BorderLayout());
        actionsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        actionsCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel actTitle = UIUtil.createHeadingLabel("Quick Campus Actions");
        actTitle.setForeground(UIUtil.ACCENT_CORAL);
        actionsCard.add(actTitle, BorderLayout.NORTH);

        JPanel actBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actBtns.setOpaque(false);

        JButton browseBtn = UIUtil.createPrimaryButton("Browse Marketplace 📚");
        browseBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("MARKETPLACE"); });

        JButton listBtn = UIUtil.createSecondaryButton("List a Book ➕");
        listBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("ADD_LISTING"); });

        JButton wishBtn = UIUtil.createSecondaryButton("My Wishlist ❤️");
        wishBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("WISHLIST"); });

        JButton recoBtn = UIUtil.createSecondaryButton("Course Syllabus Books 🎓");
        recoBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("RECOMMENDATIONS"); });

        actBtns.add(browseBtn);
        actBtns.add(listBtn);
        actBtns.add(wishBtn);
        actBtns.add(recoBtn);
        actionsCard.add(actBtns, BorderLayout.CENTER);

        container.add(actionsCard);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // RECENT TEXTBOOKS SECTION
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        recentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel recTitle = UIUtil.createHeadingLabel("✨ Recently Listed on Campus");
        JButton viewAllBtn = UIUtil.createGhostButton("View All in Marketplace →");
        viewAllBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("MARKETPLACE"); });
        recentHeader.add(recTitle, BorderLayout.WEST);
        recentHeader.add(viewAllBtn, BorderLayout.EAST);
        container.add(recentHeader);
        container.add(Box.createRigidArea(new Dimension(0, 10)));

        recentGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        recentGrid.setBackground(UIUtil.BG_PRIMARY);
        recentGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(recentGrid);

        JScrollPane scrollPane = UIUtil.createScrollPane(container);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadDashboardData() {
        Student current = SessionManager.getInstance().getCurrentStudent();
        if (current != null) {
            welcomeLabel.setText("Welcome back, " + current.getName() + "! 👋");
            deptLabel.setText(current.getDepartment() + " • Year " + current.getYearOfStudy() + ", Semester " + current.getSemester());
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private int myListingCount;
            private int pendingRequestCount;
            private int reservationCount;
            private BigDecimal walletBal;
            private List<BookListing> recentListings;

            @Override
            protected Void doInBackground() throws Exception {
                myListingCount = bookController.getMyListings().size();
                pendingRequestCount = exchangeController.getIncomingRequests().size();
                reservationCount = exchangeController.getMyReservations().size();
                walletBal = walletController.getMyBalance();
                recentListings = bookController.searchMarketplace("", null, null, null, null, "NEWEST");
                return null;
            }

            @Override
            protected void done() {
                try {
                    listingsCard.setValue(String.valueOf(myListingCount));
                    requestsCard.setValue(String.valueOf(pendingRequestCount));
                    reservationsCard.setValue(String.valueOf(reservationCount));
                    walletCard.setValue(walletBal != null ? walletBal + " Cr" : "0.00 Cr");

                    // Show up to 4 recent books
                    recentGrid.removeAll();
                    int count = 0;
                    for (BookListing l : recentListings) {
                        if (count >= 4) break;
                        BookCard card = new BookCard(l, onSelectListing, listing -> {
                            try {
                                bookController.addToWishlist(listing.getBookId());
                                UIUtil.showSuccess(StudentDashboardPanel.this, "Added to wishlist!");
                            } catch (Exception ex) {
                                UIUtil.showError(StudentDashboardPanel.this, ex.getMessage());
                            }
                        });
                        recentGrid.add(card);
                        count++;
                    }
                    recentGrid.revalidate();
                    recentGrid.repaint();
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }
}
