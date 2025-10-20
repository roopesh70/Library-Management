package com.example.library.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final Properties properties = new Properties();
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASS;
    private static final boolean DB_INIT;

    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("Sorry, unable to find application.properties");
                throw new RuntimeException("application.properties not found");
            }
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error loading application.properties", ex);
            throw new RuntimeException(ex);
        }

        DB_URL = properties.getProperty("db.url");
        DB_USER = properties.getProperty("db.user");
        DB_PASS = properties.getProperty("db.pass");
        DB_INIT = Boolean.parseBoolean(properties.getProperty("db.init", "false"));

        if (DB_INIT) {
            initSchema();
        }
    }

    /**
     * Gets a connection to the database.
     *
     * @return A database connection.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /**
     * Initializes the database schema by executing the schema.sql script.
     */
    private static void initSchema() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             InputStream in = DatabaseManager.class.getClassLoader().getResourceAsStream("schema.sql")) {

            if (in == null) {
                logger.error("schema.sql not found");
                return;
            }

            String sql = new String(in.readAllBytes());
            stmt.execute(sql);
            logger.info("Database schema initialized successfully.");

        } catch (Exception e) {
            logger.error("Failed to initialize database schema", e);
        }
    }
}
