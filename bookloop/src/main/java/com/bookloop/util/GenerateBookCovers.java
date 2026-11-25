package com.bookloop.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateBookCovers {

    public static class CoverInfo {
        public int id;
        public String title;
        public String author;
        public String edition;
        public String dept;
        public Color bgTop;
        public Color bgBottom;
        public Color accent;

        public CoverInfo(int id, String title, String author, String edition, String dept,
                         Color bgTop, Color bgBottom, Color accent) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.edition = edition;
            this.dept = dept;
            this.bgTop = bgTop;
            this.bgBottom = bgBottom;
            this.accent = accent;
        }
    }

    public static void generateAllCovers(String outputDir) {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        CoverInfo[] covers = new CoverInfo[]{
            new CoverInfo(1, "Introduction to Algorithms", "Cormen, Leiserson, Rivest, Stein", "4th Edition", "CSE",
                new Color(0x1E3A8A), new Color(0x0F172A), new Color(0x38BDF8)),
            new CoverInfo(2, "Database System Concepts", "Silberschatz, Korth, Sudarshan", "7th Edition", "CSE",
                new Color(0x065F46), new Color(0x022C22), new Color(0x34D399)),
            new CoverInfo(3, "Operating System Concepts", "Silberschatz, Galvin, Gagne", "10th Edition", "CSE",
                new Color(0x831843), new Color(0x500724), new Color(0xF472B6)),
            new CoverInfo(4, "Digital Electronics", "Roger Tokheim", "8th Edition", "ECE",
                new Color(0x4C1D95), new Color(0x2E1065), new Color(0xA78BFA)),
            new CoverInfo(5, "Signals and Systems", "Alan V. Oppenheim", "2nd Edition", "ECE",
                new Color(0x0E7490), new Color(0x164E63), new Color(0x22D3EE)),
            new CoverInfo(6, "Engineering Mechanics: Statics", "R.C. Hibbeler", "14th Edition", "ME",
                new Color(0x9A3412), new Color(0x431407), new Color(0xFB923C)),
            new CoverInfo(7, "Engineering Thermodynamics", "P.K. Nag", "6th Edition", "ME",
                new Color(0x991B1B), new Color(0x450A0A), new Color(0xF87171)),
            new CoverInfo(8, "Structural Analysis", "R.C. Hibbeler", "10th Edition", "CE",
                new Color(0x334155), new Color(0x0F172A), new Color(0x94A3B8)),
            new CoverInfo(9, "Financial Management", "I.M. Pandey", "11th Edition", "MBA",
                new Color(0x1E40AF), new Color(0x172554), new Color(0x60A5FA)),
            new CoverInfo(10, "Marketing Management", "Philip Kotler", "15th Edition", "MBA",
                new Color(0x701A75), new Color(0x4A044E), new Color(0xE879F9)),
            new CoverInfo(11, "Computer Networks", "Andrew Tanenbaum", "5th Edition", "CSE",
                new Color(0x1E3A8A), new Color(0x172554), new Color(0x67E8F9)),
            new CoverInfo(12, "Software Engineering", "Ian Sommerville", "10th Edition", "CSE",
                new Color(0x374151), new Color(0x111827), new Color(0xA5B4FC)),
            new CoverInfo(13, "Discrete Mathematics", "Kenneth Rosen", "7th Edition", "CSE",
                new Color(0x312E81), new Color(0x1E1B4B), new Color(0xC084FC)),
            new CoverInfo(14, "Microprocessors & Microcontrollers", "N. Senthil Kumar", "1st Edition", "ECE",
                new Color(0x713F12), new Color(0x451A03), new Color(0xFCD34D)),
            new CoverInfo(15, "Fluid Mechanics", "Frank White", "8th Edition", "ME",
                new Color(0x0369A1), new Color(0x0C4A6E), new Color(0x7DD3FC))
        };

        for (CoverInfo c : covers) {
            BufferedImage img = createCoverImage(c, 400, 560);
            File f = new File(dir, "book_" + c.id + ".png");
            try {
                ImageIO.write(img, "PNG", f);
            } catch (IOException e) {
                System.err.println("Failed to write cover " + c.id + ": " + e.getMessage());
            }
        }
    }

    public static BufferedImage createCoverImage(CoverInfo c, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background Gradient
        GradientPaint gp = new GradientPaint(0, 0, c.bgTop, 0, h, c.bgBottom);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, 14, 14);

        // Book spine 3D effect on left
        GradientPaint spine = new GradientPaint(0, 0, new Color(0, 0, 0, 80), 22, 0, new Color(0, 0, 0, 0));
        g2.setPaint(spine);
        g2.fillRect(0, 0, 22, h);

        // Spine highlight
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillRect(20, 0, 2, h);

        // Outer Border
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

        // Top Header Banner
        g2.setColor(new Color(255, 255, 255, 200));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString("ACADEMIC TEXTBOOK • BOOKLOOP", 34, 38);

        // Department Badge
        int badgeW = 60, badgeH = 22;
        int badgeX = w - badgeW - 20;
        int badgeY = 24;
        g2.setColor(new Color(c.accent.getRed(), c.accent.getGreen(), c.accent.getBlue(), 60));
        g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);
        g2.setColor(c.accent);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics bfm = g2.getFontMetrics();
        g2.drawString(c.dept, badgeX + (badgeW - bfm.stringWidth(c.dept)) / 2, badgeY + 15);

        // Divider Line
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawLine(34, 54, w - 20, 54);

        // Title (Word Wrapped)
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 23));
        FontMetrics fm = g2.getFontMetrics();
        int curY = 100;
        String[] words = c.title.split(" ");
        StringBuilder line = new StringBuilder();
        int maxW = w - 60;

        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxW) {
                g2.drawString(line.toString(), 34, curY);
                curY += fm.getHeight() + 4;
                line = new StringBuilder(word);
            } else {
                line.append(line.length() == 0 ? "" : " ").append(word);
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), 34, curY);
            curY += fm.getHeight();
        }

        // Accent decorative bar
        g2.setColor(c.accent);
        g2.fillRect(34, curY + 6, 48, 4);

        // Author(s)
        curY += 34;
        g2.setColor(new Color(220, 225, 235));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.drawString(c.author, 34, curY);

        // Edition Tag
        curY += 26;
        g2.setColor(c.accent);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString(c.edition, 34, curY);

        // Abstract Geometric Design in Lower Half
        int artY = h - 180;
        g2.setColor(new Color(255, 255, 255, 12));
        for (int i = 0; i < 4; i++) {
            g2.drawOval(w / 2 - 50 + i * 15, artY + i * 10, 160 - i * 20, 160 - i * 20);
        }
        g2.setColor(new Color(c.accent.getRed(), c.accent.getGreen(), c.accent.getBlue(), 25));
        g2.fill(new RoundRectangle2D.Double(34, h - 140, w - 68, 60, 12, 12));

        // Center Emblem / Icon
        g2.setColor(c.accent);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("STANDARD CURRICULUM", 48, h - 105);

        // Footer / Verified Stamp
        g2.setColor(new Color(255, 255, 255, 120));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString("Peer-to-Peer Verified Textbook • BookLoop Marketplace", 34, h - 22);

        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        String dir = args.length > 0 ? args[0] : "book_images";
        generateAllCovers(dir);
        System.out.println("Generated all 15 book covers in " + dir);
    }
}
