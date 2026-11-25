package com.bookloop.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initializeDatabaseIfMissing() throws SQLException {
        String fullUrl = DatabaseConfig.getUrl();
        String username = DatabaseConfig.getUsername();
        String password = DatabaseConfig.getPassword();

        // 1. Connect to MySQL server root (without the database name) to ensure database exists
        String serverUrl = getServerUrl(fullUrl);
        try (Connection conn = DriverManager.getConnection(serverUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS bookloop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        } catch (SQLException e) {
            LOGGER.warning("Could not create database via root URL: " + e.getMessage());
            // May still exist, proceed to check
        }

        // 2. Connect to bookloop_db directly
        try (Connection conn = DriverManager.getConnection(fullUrl, username, password)) {
            // Check if tables exist
            if (!tablesExist(conn)) {
                LOGGER.info("Tables missing in bookloop_db. Bootstrapping schema and seed data...");
                executeSqlScript(conn, "/database/schema.sql", "database/schema.sql");
                executeSqlScript(conn, "/database/seed_data.sql", "database/seed_data.sql");
                LOGGER.info("Database schema and seed data imported successfully!");
            }
        }
    }

    private static boolean tablesExist(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'users'")) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private static String getServerUrl(String fullUrl) {
        // e.g. jdbc:mysql://localhost:3306/bookloop_db?useSSL=false
        int dbIndex = fullUrl.indexOf("/bookloop_db");
        if (dbIndex != -1) {
            String prefix = fullUrl.substring(0, dbIndex + 1);
            int queryIndex = fullUrl.indexOf("?", dbIndex);
            String query = (queryIndex != -1) ? fullUrl.substring(queryIndex) : "";
            return prefix + query;
        }
        return fullUrl;
    }

    private static void executeSqlScript(Connection conn, String resourcePath, String filePath) {
        String script = readScript(resourcePath, filePath);
        if (script == null || script.trim().isEmpty()) {
            LOGGER.warning("SQL script not found: " + resourcePath);
            return;
        }

        // Split statements by semicolon
        String[] statements = script.split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                // Skip empty or comment lines
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                    continue;
                }
                try {
                    stmt.execute(trimmed);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error executing SQL statement: " + trimmed, e);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to execute script: " + resourcePath, e);
        }
    }

    private static String readScript(String resourcePath, String filePath) {
        // Try resource stream first
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {}

        // Fallback to local file
        File file = new File(filePath);
        if (file.exists()) {
            try (InputStream is = new FileInputStream(file)) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException ignored) {}
        }

        return null;
    }
}
