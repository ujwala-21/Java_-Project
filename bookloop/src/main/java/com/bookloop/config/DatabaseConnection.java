package com.bookloop.config;

import com.bookloop.exception.DatabaseException;
import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static volatile DatabaseConnection instance;
    private final BlockingQueue<Connection> pool;
    private static final int POOL_SIZE = 10;

    private DatabaseConnection() throws DatabaseException {
        pool = new ArrayBlockingQueue<>(POOL_SIZE);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            for (int i = 0; i < POOL_SIZE; i++) {
                pool.offer(createConnection());
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize connection pool: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() throws DatabaseException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword()
        );
    }

    public Connection getConnection() throws DatabaseException {
        try {
            Connection conn = pool.poll();
            if (conn == null || conn.isClosed()) {
                conn = createConnection();
            }
            return conn;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get connection: " + e.getMessage(), e);
        }
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.setAutoCommit(true);
                    if (!pool.offer(conn)) {
                        conn.close();
                    }
                }
            } catch (SQLException e) {
                LOGGER.warning("Error releasing connection: " + e.getMessage());
            }
        }
    }

    public static void reset() {
        instance = null;
    }
}
