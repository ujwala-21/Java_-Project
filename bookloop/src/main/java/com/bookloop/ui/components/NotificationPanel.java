package com.bookloop.ui.components;

import com.bookloop.dao.NotificationDAO;
import com.bookloop.model.Notification;
import com.bookloop.util.DateUtil;
import com.bookloop.util.SessionManager;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificationPanel extends JPanel {
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private JPanel listPanel;
    private JLabel countLabel;

    public NotificationPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadNotifications();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Campus Notifications 🔔");
        countLabel = UIUtil.createMutedLabel("Real-time updates on requests, reservations, and credit rewards");
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
        refreshBtn.addActionListener(e -> loadNotifications());
        tb.add(refreshBtn);

        JButton markAllBtn = UIUtil.createPrimaryButton("Mark All as Read ✓");
        markAllBtn.addActionListener(e -> markAllRead());
        tb.add(markAllBtn);

        container.add(tb);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Notifications List Card
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UIUtil.BG_PRIMARY);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane sp = UIUtil.createScrollPane(listPanel);
        sp.setPreferredSize(new Dimension(850, 440));
        sp.setBorder(null);
        container.add(sp);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    public void loadNotifications() {
        int userId = SessionManager.getInstance().getCurrentUserId();

        SwingWorker<List<Notification>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Notification> doInBackground() throws Exception {
                return notificationDAO.getNotificationsForUser(userId);
            }

            @Override
            protected void done() {
                try {
                    List<Notification> list = get();
                    displayNotifications(list);
                } catch (Exception ex) {
                    countLabel.setText("Failed to load notifications: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayNotifications(List<Notification> list) {
        listPanel.removeAll();
        int unread = (int) list.stream().filter(n -> !n.isRead()).count();
        countLabel.setText(unread > 0 ? unread + " unread notification(s)" : "All caught up!");

        if (list.isEmpty()) {
            JPanel empty = UIUtil.createCard();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

            JLabel icon = new JLabel("🔔");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel msg = UIUtil.createHeadingLabel("No notifications yet");
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel sub = UIUtil.createMutedLabel("When other students request or accept your book trades, you'll see alerts here.");
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(icon);
            empty.add(Box.createRigidArea(new Dimension(0, 8)));
            empty.add(msg);
            empty.add(Box.createRigidArea(new Dimension(0, 4)));
            empty.add(sub);
            listPanel.add(empty);
        } else {
            for (Notification n : list) {
                JPanel card = UIUtil.createCard();
                card.setLayout(new BorderLayout(14, 4));
                card.setMaximumSize(new Dimension(860, 80));

                if (!n.isRead()) {
                    card.setBackground(new Color(0xFFFAF6));
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, UIUtil.ACCENT_PEACH),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)
                    ));
                }

                JPanel content = new JPanel();
                content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                content.setOpaque(false);

                JLabel title = UIUtil.createBodyLabel("<b>" + n.getTitle() + "</b>");
                JLabel msg = UIUtil.createMutedLabel(n.getMessage());
                content.add(title);
                content.add(Box.createRigidArea(new Dimension(0, 2)));
                content.add(msg);

                card.add(content, BorderLayout.CENTER);

                JLabel time = UIUtil.createMutedLabel(DateUtil.getRelativeTime(n.getCreatedAt()));
                card.add(time, BorderLayout.EAST);

                listPanel.add(card);
                listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void markAllRead() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return notificationDAO.markAllAsRead(userId);
            }

            @Override
            protected void done() {
                loadNotifications();
            }
        };
        worker.execute();
    }
}
