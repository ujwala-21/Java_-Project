package com.bookloop.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageUtil {
    private static final String UPLOAD_DIR = "book_images";

    static {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String saveImage(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) return null;
        String ext = "";
        String name = sourceFile.getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            ext = name.substring(dot);
        }
        String filename = UUID.randomUUID().toString() + ext;
        Path targetPath = Path.of(UPLOAD_DIR, filename);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toString();
    }

    public static ImageIcon loadAndScaleImage(String imagePath, int width, int height) {
        return loadAndScaleImage(imagePath, 0, width, height);
    }

    public static ImageIcon loadAndScaleImage(String imagePath, int bookId, int width, int height) {
        // 1. Try explicit imagePath if given and exists
        if (imagePath != null && !imagePath.isEmpty()) {
            File f = new File(imagePath);
            if (f.exists()) {
                ImageIcon icon = readAndScale(f, width, height);
                if (icon != null) return icon;
            }
        }

        // 2. Fallback to pre-generated book covers if bookId > 0
        if (bookId > 0) {
            File f = new File(UPLOAD_DIR, "book_" + bookId + ".png");
            if (f.exists()) {
                ImageIcon icon = readAndScale(f, width, height);
                if (icon != null) return icon;
            }
        }

        // 3. Fallback to styled placeholder
        return createPlaceholder(width, height, "📖");
    }

    private static ImageIcon readAndScale(File file, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(file);
            if (img != null) {
                Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static ImageIcon createPlaceholder(int width, int height, String symbol) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Soft pastel gradient
        GradientPaint gp = new GradientPaint(0, 0, new Color(0xFEECE2), width, height, new Color(0xF9D5C8));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, width, height, 14, 14);

        // Spine effect
        g2.setColor(new Color(0, 0, 0, 25));
        g2.fillRect(0, 0, Math.max(8, width / 12), height);

        // Border
        g2.setColor(UIUtil.BORDER_COLOR);
        g2.drawRoundRect(0, 0, width - 1, height - 1, 14, 14);

        // Center Icon
        g2.setColor(UIUtil.ACCENT_CORAL);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.max(18, width / 4)));
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(symbol)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent() - 10;
        g2.drawString(symbol, x, y);

        // Subtitle
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(9, width / 18)));
        g2.setColor(UIUtil.TEXT_SECONDARY);
        FontMetrics fm2 = g2.getFontMetrics();
        String sub = "BOOK COVER";
        int subX = (width - fm2.stringWidth(sub)) / 2;
        g2.drawString(sub, subX, y + 26);

        g2.dispose();
        return new ImageIcon(img);
    }
}
