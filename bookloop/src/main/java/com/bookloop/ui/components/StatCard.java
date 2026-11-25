package com.bookloop.ui.components;

import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;

    public StatCard(String icon, String title, String initialValue, String subtitle, Color accentColor) {
        setLayout(new BorderLayout(12, 8));
        setBackground(UIUtil.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtil.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        setPreferredSize(new Dimension(210, 105));
        setMaximumSize(new Dimension(240, 115));

        // LEFT ICON
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLbl.setForeground(accentColor);
        add(iconLbl, BorderLayout.WEST);

        // RIGHT CONTENT
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIUtil.FONT_SMALL_BOLD);
        titleLbl.setForeground(UIUtil.TEXT_SECONDARY);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(UIUtil.TEXT_PRIMARY);

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UIUtil.FONT_CAPTION);
        subtitleLabel.setForeground(accentColor);

        content.add(titleLbl);
        content.add(Box.createRigidArea(new Dimension(0, 2)));
        content.add(valueLabel);
        content.add(Box.createRigidArea(new Dimension(0, 2)));
        content.add(subtitleLabel);

        add(content, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }
}
