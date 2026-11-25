package com.bookloop.ui.components;

import com.bookloop.controller.ExchangeController;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class ReviewDialog extends JDialog {
    private final int reviewedUserId;
    private final String reviewedUserName;
    private final Integer transactionId;
    private final Runnable onSuccess;

    private JComboBox<String> starCombo;
    private JTextArea commentArea;

    public ReviewDialog(Frame parent, int reviewedUserId, String reviewedUserName, Integer transactionId, Runnable onSuccess) {
        super(parent, "Review & Rate Partner - BookLoop", true);
        this.reviewedUserId = reviewedUserId;
        this.reviewedUserName = reviewedUserName;
        this.transactionId = transactionId;
        this.onSuccess = onSuccess;

        setSize(460, 340);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIUtil.CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel titleLbl = UIUtil.createTitleLabel("Rate Your Exchange Experience ⭐");
        JLabel subLbl = UIUtil.createMutedLabel("Review your exchange partner: " + reviewedUserName);
        panel.add(titleLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(subLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        JLabel ratingLbl = UIUtil.createBodyLabel("Star Rating (1 to 5 stars):");
        panel.add(ratingLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        starCombo = UIUtil.createComboBox(new String[]{
            "⭐⭐⭐⭐⭐ (5 Stars - Excellent)",
            "⭐⭐⭐⭐ (4 Stars - Good)",
            "⭐⭐⭐ (3 Stars - Average)",
            "⭐⭐ (2 Stars - Below Average)",
            "⭐ (1 Star - Poor)"
        });
        starCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(starCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel commentLbl = UIUtil.createBodyLabel("Feedback Comments (optional):");
        panel.add(commentLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        commentArea = UIUtil.createTextArea("Was the book in described condition? Punctual meetup?", 3);
        JScrollPane sp = new JScrollPane(commentArea);
        sp.setPreferredSize(new Dimension(380, 70));
        panel.add(sp);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);

        JButton cancelBtn = UIUtil.createGhostButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton submitBtn = UIUtil.createPrimaryButton("Submit Review ⭐");
        submitBtn.addActionListener(e -> submitReview());

        btnBar.add(cancelBtn);
        btnBar.add(submitBtn);
        panel.add(btnBar);

        setContentPane(panel);
    }

    private void submitReview() {
        int stars = 5 - starCombo.getSelectedIndex(); // 5 down to 1
        String comment = commentArea.getText().trim();
        if (comment.equals("Was the book in described condition? Punctual meetup?")) {
            comment = "";
        }

        ExchangeController ec = new ExchangeController();
        final String fComment = comment;

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return ec.submitReview(reviewedUserId, transactionId, stars, fComment);
            }

            @Override
            protected void done() {
                try {
                    int id = get();
                    if (id > 0) {
                        UIUtil.showSuccess(ReviewDialog.this, "Thank you for supporting community trust! Review submitted.");
                        dispose();
                        if (onSuccess != null) onSuccess.run();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(ReviewDialog.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
