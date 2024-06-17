package me.ishyro.core.api;

import java.io.File;
import java.sql.*;

public class SQLHandler {

    private static Connection connection = null;
    private static boolean debug = false;
    private final String filePath;

    public SQLHandler(final String filePath) {
        this.filePath = filePath;
        debug = false;
    }

    public SQLHandler(final String filePath, boolean debugMode) {
        this.filePath = filePath;
        debug = debugMode;
    }

    public static synchronized Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Connection has not been established. Call connect() first.");
        }
        return connection;
    }

    public static synchronized ResultSet sqlQuery(final String sql) {
        if (debug)
            System.out.println("SQL QUERY: " + sql);

        ResultSet rs = null;
        int retries = 5;
        while (retries > 0) {
            try {
                final Statement statement = getConnection().createStatement();
                if (debug)
                    System.out.println("EXECUTING: " + sql);
                rs = statement.executeQuery(sql);
                if (debug)
                    System.out.println("SUCCESS: " + sql);
                return rs;
            } catch (SQLException e) {
                if (e.getErrorCode() == 5) { // SQLITE_BUSY
                    retries--;
                    try {
                        Thread.sleep(100); // Wait for 100ms before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    e.printStackTrace();
                    throw new IllegalStateException("Failed to execute query: " + sql);
                }
            }
        }
        throw new IllegalStateException("Failed to execute query after retries: " + sql);
    }

    public static synchronized void sqlUpdate(final String sql) {
        if (debug)
            System.out.println("SQL UPDATE: " + sql);
        int retries = 5;
        while (retries > 0) {
            try {
                final PreparedStatement pstmt = getConnection().prepareStatement(sql);
                pstmt.executeUpdate();
                pstmt.close();
                if (debug)
                    System.out.println("SUCCESS: " + sql);
                return;
            } catch (SQLException e) {
                if (e.getErrorCode() == 5) { // SQLITE_BUSY
                    retries--;
                    try {
                        Thread.sleep(100); // Wait for 100ms before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    e.printStackTrace();
                    throw new IllegalStateException("Failed to execute update: " + sql);
                }
            }
        }
        throw new IllegalStateException("Failed to execute update after retries: " + sql);
    }

    public synchronized void connect(final String databaseName) {
        if (connection != null) {
            return; // Already connected
        }
        try {
            Class.forName("org.sqlite.JDBC");
            final String URL = "jdbc:sqlite:" + filePath + File.separator + databaseName + ".db";
            connection = DriverManager.getConnection(URL);
            if (connection != null) {
                onConnect();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onConnect() {
        // Override in subclasses if needed
    }

    public synchronized void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
            connection = null;
            onDisconnect();
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void onDisconnect() {
        // Override in subclasses if needed
    }
}
