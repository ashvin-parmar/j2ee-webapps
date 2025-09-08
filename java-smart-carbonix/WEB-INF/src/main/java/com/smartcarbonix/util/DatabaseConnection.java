package com.smartcarbonix.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static BasicDataSource dataSource;
    private static final String DB_URL = System.getenv("DATABASE_URL");
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/smartcarbonix";
    private static final String USERNAME = System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "postgres";
    private static final String PASSWORD = System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : "password";
    
    static {
        initializeDataSource();
    }
    
    private static void initializeDataSource() {
        dataSource = new BasicDataSource();
        
        // Use DATABASE_URL if available (for Replit), otherwise use default
        String url = DB_URL != null ? DB_URL : DEFAULT_URL;
        dataSource.setUrl(url);
        
        // Set connection properties
        dataSource.setDriverClassName("org.postgresql.Driver");
        
        // Only set username/password if not using full DATABASE_URL
        if (DB_URL == null || !DB_URL.contains("://")) {
            dataSource.setUsername(USERNAME);
            dataSource.setPassword(PASSWORD);
        }
        
        // Connection pool settings
        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(20);
        dataSource.setMaxIdle(10);
        dataSource.setMinIdle(5);
        
        // Connection validation
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(30000);
        dataSource.setMinEvictableIdleTimeMillis(60000);
        
        System.out.println("Database connection pool initialized with URL: " + 
            (url.contains("@") ? url.substring(0, url.indexOf("@")) + "@***" : url));
    }
    
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void closeDataSource() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                System.err.println("Error closing data source: " + e.getMessage());
            }
        }
    }
    
    // Test connection method
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}