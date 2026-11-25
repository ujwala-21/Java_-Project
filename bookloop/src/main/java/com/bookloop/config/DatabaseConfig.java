package com.bookloop.config;

import java.io.*;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "db.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (InputStream is = new FileInputStream(configFile)) {
                properties.load(is);
            } catch (IOException e) {
                loadDefaults();
            }
        } else {
            loadDefaults();
            saveDefaults(configFile);
        }
    }

    private static void loadDefaults() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/bookloop_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.pool.size", "10");
    }

    private static void saveDefaults(File file) {
        try (OutputStream os = new FileOutputStream(file)) {
            properties.store(os, "BookLoop Database Configuration");
        } catch (IOException ignored) {}
    }

    public static String getUrl() { return properties.getProperty("db.url"); }
    public static String getUsername() { return properties.getProperty("db.username"); }
    public static String getPassword() { return properties.getProperty("db.password"); }
    public static int getPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.size", "10"));
    }

    public static void updateConfig(String url, String username, String password) {
        properties.setProperty("db.url", url);
        properties.setProperty("db.username", username);
        properties.setProperty("db.password", password);
        saveDefaults(new File(CONFIG_FILE));
    }
}
