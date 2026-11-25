package com.bookloop.ui.components;

import com.bookloop.model.User;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Sidebar extends JPanel {
    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final List<SidebarButton> buttons = new ArrayList<>();
    private String activeKey = "DASHBOARD";

    public Sidebar(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setLayout(new BorderLayout());
        setBackground(UIUtil.SIDEBAR_BG);
        setPreferredSize(new Dimension(240, 720));
        initComponents();
    }

    private void initComponents() {
        // TOP LOGO AREA
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JLabel logoLbl = new JLabel("📚 BookLoop");
        logoLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLbl.setForeground(Color.WHITE);
        logoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagLbl = new JLabel("Academic Book Exchange");
        tagLbl.setFont(UIUtil.FONT_CAPTION);
        tagLbl.setForeground(new Color(0xD8B8C8));
        tagLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(logoLbl);
        topPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        topPanel.add(tagLbl);
        add(topPanel, BorderLayout.NORTH);

        // NAVIGATION MENU ITEMS (Scrollable if needed)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        if (isAdmin) {
            addMenuItem(menuPanel, "🛡️ Admin Dashboard", "ADMIN_DASHBOARD");
            addMenuItem(menuPanel, "👥 Student Accounts", "STUDENTS");
            addMenuItem(menuPanel, "📋 Manage Listings", "LISTINGS");
            addMenuItem(menuPanel, "📊 Analytics & Reports", "ANALYTICS");
            addMenuItem(menuPanel, "🔔 Campus Alerts", "NOTIFICATIONS");
        } else {
            addMenuItem(menuPanel, "🏠 Dashboard", "DASHBOARD");
            addMenuItem(menuPanel, "📚 Marketplace", "MARKETPLACE");
            addMenuItem(menuPanel, "➕ List a Book", "ADD_LISTING");
            addMenuItem(menuPanel, "📋 My Listings", "MY_LISTINGS");
            addMenuItem(menuPanel, "❤️ Wishlist", "WISHLIST");
            addMenuItem(menuPanel, "🤝 Exchange Requests", "REQUESTS");
            addMenuItem(menuPanel, "🔄 Smart Matches", "CIRCULAR");
            addMenuItem(menuPanel, "⏳ Reservations", "RESERVATIONS");
            addMenuItem(menuPanel, "💳 Credit Wallet", "WALLET");
            addMenuItem(menuPanel, "🎓 Recommendations", "RECOMMENDATIONS");
            addMenuItem(menuPanel, "📜 History", "HISTORY");
            addMenuItem(menuPanel, "🔔 Notifications", "NOTIFICATIONS");
            addMenuItem(menuPanel, "👤 Profile", "PROFILE");
        }

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // BOTTOM LOGOUT BUTTON
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 20, 16));

        JButton logoutBtn = new JButton("Sign Out 🚪");
        logoutBtn.setFont(UIUtil.FONT_BUTTON);
        logoutBtn.setForeground(new Color(0xFFAAA6));
        logoutBtn.setBackground(new Color(0x3E2436));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            if (UIUtil.showConfirm(this, "Are you sure you want to sign out?")) {
                if (onLogout != null) onLogout.run();
            }
        });
        bottomPanel.add(logoutBtn, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Select initial
        setActive(isAdmin ? "ADMIN_DASHBOARD" : "DASHBOARD");
    }

    private void addMenuItem(JPanel container, String text, String key) {
        SidebarButton btn = new SidebarButton(text, key);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActive(key);
                if (onNavigate != null) onNavigate.accept(key);
            }
        });
        buttons.add(btn);
        container.add(btn);
        container.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    public void setActive(String key) {
        this.activeKey = key;
        for (SidebarButton btn : buttons) {
            btn.setActive(btn.key.equals(key));
        }
    }

    private static class SidebarButton extends JPanel {
        private final String key;
        private final JLabel label;
        private boolean active = false;

        public SidebarButton(String text, String key) {
            this.key = key;
            setLayout(new BorderLayout());
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setPreferredSize(new Dimension(200, 38));
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setForeground(UIUtil.SIDEBAR_ITEM);
            add(label, BorderLayout.WEST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) {
                        setBackground(UIUtil.SIDEBAR_ITEM_HOVER);
                        setOpaque(true);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) {
                        setOpaque(false);
                        repaint();
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                setOpaque(true);
                setBackground(new Color(0x482B42));
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setForeground(UIUtil.ACCENT_PEACH);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, UIUtil.ACCENT_PEACH),
                    BorderFactory.createEmptyBorder(8, 11, 8, 14)
                ));
            } else {
                setOpaque(false);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setForeground(UIUtil.SIDEBAR_ITEM);
                setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            }
            repaint();
        }
    }
}
