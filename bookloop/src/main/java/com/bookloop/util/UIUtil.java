package com.bookloop.util;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIUtil {
    // === COLOR PALETTE ===
    public static final Color BG_PRIMARY = new Color(0xFDF8F5);      // Warm peach neutral background
    public static final Color BG_SECONDARY = new Color(0xF6ECE5);    // Slightly deeper warm neutral
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_BG_ALT = new Color(0xFFFBF9);

    public static final Color ACCENT_PEACH = new Color(0xF4A261);    // Primary accent - peach
    public static final Color ACCENT_CORAL = new Color(0xE76F51);    // Secondary accent - soft coral
    public static final Color ACCENT_ROSE = new Color(0xD67D7D);     // Dusty rose
    public static final Color ACCENT_MAUVE = new Color(0xA46877);    // Muted mauve
    public static final Color ACCENT_PLUM = new Color(0x382335);     // Deep greyish plum

    public static final Color TEXT_PRIMARY = new Color(0x2D1B2E);    // Dark plum/charcoal
    public static final Color TEXT_SECONDARY = new Color(0x7A6577);  // Muted grey
    public static final Color TEXT_MUTED = new Color(0xAB9EA9);      // Light muted
    public static final Color TEXT_ON_ACCENT = Color.WHITE;

    public static final Color BORDER_COLOR = new Color(0xE8DDE5);
    public static final Color BORDER_FOCUS = ACCENT_PEACH;

    public static final Color SUCCESS = new Color(0x40916C);
    public static final Color WARNING = new Color(0xF4A261);
    public static final Color ERROR = new Color(0xD90429);
    public static final Color INFO = new Color(0x2A9D8F);

    public static final Color SIDEBAR_BG = new Color(0x2B1A28);      // Deep plum sidebar
    public static final Color SIDEBAR_ITEM = new Color(0xF2EAE9);
    public static final Color SIDEBAR_ITEM_ACTIVE = ACCENT_PEACH;
    public static final Color SIDEBAR_ITEM_HOVER = new Color(0x3E273A);

    // === FONTS ===
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_CAPTION = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 20);

    // === DIMENSIONS ===
    public static final int CORNER_RADIUS = 12;
    public static final int BUTTON_HEIGHT = 38;
    public static final int INPUT_HEIGHT = 38;
    public static final Insets CARD_PADDING = new Insets(16, 20, 16, 20);

    public static void applyBookLoopTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        UIManager.put("Panel.background", BG_PRIMARY);
        UIManager.put("OptionPane.background", CARD_BG);
        UIManager.put("Button.font", FONT_BUTTON);
        UIManager.put("Button.arc", CORNER_RADIUS * 2);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.alternateRowBackground", new Color(0xFCF8F5));
        UIManager.put("Table.selectionBackground", new Color(0xFEEDDF));
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 8);
    }

    // === BUTTON FACTORIES ===
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_PEACH);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, BUTTON_HEIGHT));
        addHoverEffect(btn, ACCENT_PEACH, ACCENT_CORAL);
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(ACCENT_CORAL);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(ACCENT_CORAL, 1));
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, BUTTON_HEIGHT));
        addHoverEffect(btn, Color.WHITE, new Color(0xFFF3F0));
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, BUTTON_HEIGHT));
        addHoverEffect(btn, SUCCESS, new Color(0x2D6A4F));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ERROR);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, BUTTON_HEIGHT));
        addHoverEffect(btn, ERROR, new Color(0xA00020));
        return btn;
    }

    public static JButton createGhostButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(null);
        btn.setForeground(TEXT_SECONDARY);
        btn.setFont(FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setForeground(ACCENT_PEACH); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setForeground(TEXT_SECONDARY); }
        });
        return btn;
    }

    private static void addHoverEffect(JButton btn, Color normal, Color hover) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(normal); }
        });
    }

    // === PANEL FACTORIES ===
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        return card;
    }

    public static JPanel createRoundedCard(int radius) {
        return new RoundedPanel(radius, CARD_BG);
    }

    public static JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        return panel;
    }

    // === LABEL FACTORIES ===
    public static JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    // === INPUT FACTORIES ===
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, INPUT_HEIGHT));
        addPlaceholder(field, placeholder);
        addFocusBorder(field);
        return field;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, INPUT_HEIGHT));
        addFocusBorder(field);
        return field;
    }

    public static JTextArea createTextArea(String placeholder, int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setFont(FONT_BODY);
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return area;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, INPUT_HEIGHT));
        return combo;
    }

    private static void addPlaceholder(JTextField field, String placeholder) {
        if (placeholder == null || placeholder.isEmpty()) return;
        field.setText(placeholder);
        field.setForeground(TEXT_MUTED);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_MUTED);
                }
            }
        });
    }

    private static void addFocusBorder(JComponent field) {
        Border normal = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        );
        Border focused = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_FOCUS, 2),
            BorderFactory.createEmptyBorder(5, 11, 5, 11)
        );
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.setBorder(focused); }
            @Override
            public void focusLost(FocusEvent e) { field.setBorder(normal); }
        });
    }

    // === TABLE STYLING ===
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(Color.WHITE);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xFEEDDF));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(FONT_BODY_BOLD);
        table.getTableHeader().setBackground(BG_SECONDARY);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBackground(Color.WHITE);
        return sp;
    }

    // === STATUS BADGE ===
    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setFont(FONT_SMALL_BOLD);
        badge.setForeground(fg);
        badge.setBackground(bg);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return badge;
    }

    public static JLabel createStatusBadge(String status) {
        if (status == null) status = "UNKNOWN";
        Color bg, fg;
        switch (status.toUpperCase()) {
            case "AVAILABLE" -> { bg = new Color(0xDEF7EC); fg = new Color(0x03543F); }
            case "RESERVED" -> { bg = new Color(0xFEF3C7); fg = new Color(0x92400E); }
            case "REQUESTED" -> { bg = new Color(0xEDE9FE); fg = new Color(0x5B21B6); }
            case "COMPLETED" -> { bg = new Color(0xE0E7FF); fg = new Color(0x3730A3); }
            case "CANCELLED", "REJECTED" -> { bg = new Color(0xFEE2E2); fg = new Color(0x991B1B); }
            case "PENDING" -> { bg = new Color(0xFFEDD5); fg = new Color(0x9A3412); }
            case "ACCEPTED" -> { bg = new Color(0xDEF7EC); fg = new Color(0x03543F); }
            case "SELL" -> { bg = new Color(0xF3E8FF); fg = new Color(0x6B21A8); }
            case "EXCHANGE" -> { bg = new Color(0xFCE7F3); fg = new Color(0x9D174D); }
            case "RENT" -> { bg = new Color(0xE0F2FE); fg = new Color(0x0369A1); }
            case "DONATE" -> { bg = new Color(0xDCFCE7); fg = new Color(0x15803D); }
            case "NEW" -> { bg = new Color(0xDEF7EC); fg = new Color(0x03543F); }
            case "EXCELLENT" -> { bg = new Color(0xE0F2FE); fg = new Color(0x0369A1); }
            case "GOOD" -> { bg = new Color(0xFEF3C7); fg = new Color(0x92400E); }
            case "ACCEPTABLE" -> { bg = new Color(0xFFEDD5); fg = new Color(0x9A3412); }
            default -> { bg = BORDER_COLOR; fg = TEXT_SECONDARY; }
        }
        return createBadge(status, bg, fg);
    }

    // === ROUNDED PANEL HELPER ===
    public static class RoundedPanel extends JPanel {
        private final int radius;
        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            setBackground(bg);
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "BookLoop Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "BookLoop Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "BookLoop Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "BookLoop Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
