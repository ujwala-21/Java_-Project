package com.bookloop;

import com.bookloop.config.DatabaseConfig;
import com.bookloop.config.DatabaseConnection;
import com.bookloop.ui.LoginFrame;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("flatlaf.animation", "true");

        SwingUtilities.invokeLater(() -> {
            UIUtil.applyBookLoopTheme();

            boolean connected = false;
            while (!connected) {
                try {
                    var conn = DatabaseConnection.getInstance().getConnection();
                    DatabaseConnection.getInstance().releaseConnection(conn);
                    connected = true;
                } catch (Exception e) {
                    int choice = JOptionPane.showOptionDialog(null,
                        "Could not connect to MySQL database (" + DatabaseConfig.getUrl() + ").\n\n" +
                        "1. Ensure MySQL Server is running.\n" +
                        "2. Ensure 'bookloop_db' database is imported from 'database/schema.sql'.\n" +
                        "3. Update your credentials below if needed.\n\n" +
                        "Technical Error: " + e.getMessage(),
                        "BookLoop - Database Setup",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new String[]{"Retry Connection", "Configure DB Credentials", "Exit"},
                        "Retry Connection");

                    if (choice == 1) { // Configure
                        JTextField urlField = new JTextField(DatabaseConfig.getUrl());
                        JTextField userField = new JTextField(DatabaseConfig.getUsername());
                        JPasswordField passField = new JPasswordField(DatabaseConfig.getPassword());

                        Object[] message = {
                            "JDBC URL:", urlField,
                            "Username:", userField,
                            "Password:", passField
                        };

                        int opt = JOptionPane.showConfirmDialog(null, message, "Update MySQL Configuration", JOptionPane.OK_CANCEL_OPTION);
                        if (opt == JOptionPane.OK_OPTION) {
                            DatabaseConfig.updateConfig(urlField.getText().trim(), userField.getText().trim(), new String(passField.getPassword()));
                            DatabaseConnection.reset();
                        }
                    } else if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                        System.exit(0);
                    }
                }
            }

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
