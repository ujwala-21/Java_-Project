package com.bookloop.ui;

import com.bookloop.model.BookListing;
import com.bookloop.model.User;
import com.bookloop.ui.admin.AdminDashboardPanel;
import com.bookloop.ui.admin.AnalyticsPanel;
import com.bookloop.ui.admin.ListingManagementPanel;
import com.bookloop.ui.admin.UserManagementPanel;
import com.bookloop.ui.components.NotificationPanel;
import com.bookloop.ui.components.Sidebar;
import com.bookloop.ui.student.*;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class MainDashboardFrame extends JFrame {
    private Sidebar sidebar;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // View panels
    private StudentDashboardPanel studentDashboardPanel;
    private MarketplacePanel marketplacePanel;
    private AddListingPanel addListingPanel;
    private MyListingsPanel myListingsPanel;
    private WishlistPanel wishlistPanel;
    private ExchangeRequestsPanel requestsPanel;
    private CircularExchangePanel circularExchangePanel;
    private ReservationPanel reservationPanel;
    private WalletPanel walletPanel;
    private RecommendationsPanel recommendationsPanel;
    private HistoryPanel historyPanel;
    private NotificationPanel notificationPanel;
    private ProfilePanel profilePanel;

    // Admin panels
    private AdminDashboardPanel adminDashboardPanel;
    private UserManagementPanel userManagementPanel;
    private ListingManagementPanel listingManagementPanel;
    private AnalyticsPanel analyticsPanel;

    public MainDashboardFrame() {
        setTitle("BookLoop - Smart Student Book Exchange");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtil.BG_PRIMARY);

        // Sidebar
        sidebar = new Sidebar(this::showPage, this::handleLogout);
        root.add(sidebar, BorderLayout.WEST);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UIUtil.BG_PRIMARY);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        if (isAdmin) {
            adminDashboardPanel = new AdminDashboardPanel(this::showPage);
            userManagementPanel = new UserManagementPanel();
            listingManagementPanel = new ListingManagementPanel();
            analyticsPanel = new AnalyticsPanel();
            notificationPanel = new NotificationPanel();

            cardPanel.add(adminDashboardPanel, "ADMIN_DASHBOARD");
            cardPanel.add(userManagementPanel, "STUDENTS");
            cardPanel.add(listingManagementPanel, "LISTINGS");
            cardPanel.add(analyticsPanel, "ANALYTICS");
            cardPanel.add(notificationPanel, "NOTIFICATIONS");

            cardLayout.show(cardPanel, "ADMIN_DASHBOARD");
        } else {
            studentDashboardPanel = new StudentDashboardPanel(this::showPage, this::showBookDetails);
            marketplacePanel = new MarketplacePanel(this::showBookDetails);
            addListingPanel = new AddListingPanel(() -> showPage("MY_LISTINGS"));
            myListingsPanel = new MyListingsPanel();
            wishlistPanel = new WishlistPanel(() -> showPage("MARKETPLACE"), this::showBookDetails);
            requestsPanel = new ExchangeRequestsPanel(() -> showPage("RESERVATIONS"));
            circularExchangePanel = new CircularExchangePanel();
            reservationPanel = new ReservationPanel(() -> showPage("HISTORY"));
            walletPanel = new WalletPanel();
            recommendationsPanel = new RecommendationsPanel(() -> showPage("MARKETPLACE"));
            historyPanel = new HistoryPanel();
            notificationPanel = new NotificationPanel();
            profilePanel = new ProfilePanel();

            cardPanel.add(studentDashboardPanel, "DASHBOARD");
            cardPanel.add(marketplacePanel, "MARKETPLACE");
            cardPanel.add(addListingPanel, "ADD_LISTING");
            cardPanel.add(myListingsPanel, "MY_LISTINGS");
            cardPanel.add(wishlistPanel, "WISHLIST");
            cardPanel.add(requestsPanel, "REQUESTS");
            cardPanel.add(circularExchangePanel, "CIRCULAR");
            cardPanel.add(reservationPanel, "RESERVATIONS");
            cardPanel.add(walletPanel, "WALLET");
            cardPanel.add(recommendationsPanel, "RECOMMENDATIONS");
            cardPanel.add(historyPanel, "HISTORY");
            cardPanel.add(notificationPanel, "NOTIFICATIONS");
            cardPanel.add(profilePanel, "PROFILE");

            cardLayout.show(cardPanel, "DASHBOARD");
        }

        root.add(cardPanel, BorderLayout.CENTER);
        setContentPane(root);
    }

    public void showPage(String pageKey) {
        sidebar.setActive(pageKey);

        // Auto-refresh data on navigation
        switch (pageKey) {
            case "DASHBOARD" -> { if (studentDashboardPanel != null) studentDashboardPanel.loadDashboardData(); }
            case "MARKETPLACE" -> { if (marketplacePanel != null) marketplacePanel.loadMarketplaceListings(); }
            case "MY_LISTINGS" -> { if (myListingsPanel != null) myListingsPanel.loadMyListings(); }
            case "WISHLIST" -> { if (wishlistPanel != null) wishlistPanel.loadWishlist(); }
            case "REQUESTS" -> { if (requestsPanel != null) requestsPanel.loadAllRequests(); }
            case "CIRCULAR" -> { if (circularExchangePanel != null) circularExchangePanel.runMatchingAlgorithms(); }
            case "RESERVATIONS" -> { if (reservationPanel != null) reservationPanel.loadReservations(); }
            case "WALLET" -> { if (walletPanel != null) walletPanel.loadWalletData(); }
            case "HISTORY" -> { if (historyPanel != null) historyPanel.loadHistory(); }
            case "NOTIFICATIONS" -> { if (notificationPanel != null) notificationPanel.loadNotifications(); }
            case "ADMIN_DASHBOARD" -> { if (adminDashboardPanel != null) adminDashboardPanel.loadStatistics(); }
            case "STUDENTS" -> { if (userManagementPanel != null) userManagementPanel.loadStudents(); }
            case "LISTINGS" -> { if (listingManagementPanel != null) listingManagementPanel.loadAllListings(); }
            case "ANALYTICS" -> { if (analyticsPanel != null) analyticsPanel.loadAnalyticsData(); }
        }

        cardLayout.show(cardPanel, pageKey);
    }

    public void showBookDetails(BookListing listing) {
        BookDetailsPanel detailsPanel = new BookDetailsPanel(listing, () -> showPage("MARKETPLACE"));
        cardPanel.add(detailsPanel, "BOOK_DETAILS");
        cardLayout.show(cardPanel, "BOOK_DETAILS");
    }

    private void handleLogout() {
        SessionManager.getInstance().logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
