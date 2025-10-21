package com.example.library.cli;

import com.example.library.dao.*;
import com.example.library.db.DatabaseManager;
import com.example.library.model.*;
import com.example.library.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class MainApp {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        logger.info("Library Management System starting...");

        // Load configuration
        Properties props = new Properties();
        double fineRate;
        try (InputStream input = MainApp.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.warn("Warning: application.properties not found. Using default fine rate.");
            } else {
                props.load(input);
            }
        } catch (java.io.IOException e) {
            logger.error("Error reading application.properties. Using default fine rate.", e);
        }

        try {
            fineRate = Double.parseDouble(props.getProperty("fine.rate.per.day", "0.50"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid fine rate in properties. Using default: 0.50.", e);
            fineRate = 0.50;
        }

        // Initialize components
        DatabaseManager dbManager = new DatabaseManager();
        BookDao bookDao = new JdbcBookDao(dbManager);
        UserDao userDao = new JdbcUserDao(dbManager);
        TransactionDao transactionDao = new JdbcTransactionDao(dbManager);

        FineManager fineManager = new FineManager(fineRate);
        NotificationService notificationService = new NotificationService();
        AuthenticationService authService = new AuthenticationService(userDao);
        LibraryService libraryService = new LibraryService(bookDao, transactionDao, fineManager, notificationService);
        ReportGenerator reportGenerator = new ReportGenerator(bookDao, userDao, transactionDao);

        // Seed initial data (for demonstration)
        seedData(userDao, bookDao);

        // Start CLI
        try (Scanner scanner = new Scanner(System.in)) {
            new MainMenu(scanner, authService, libraryService, reportGenerator, bookDao, userDao).run();
        }

        logger.info("Library Management System shutting down.");
    }

    private static void seedData(UserDao userDao, BookDao bookDao) {
        // Add a student and a librarian if they don't exist
        if (userDao.findByName("student1").isEmpty()) {
            userDao.addUser(new Student("student1", "Computer Science", 3));
        }
        if (userDao.findByName("librarian1").isEmpty()) {
            userDao.addUser(new Librarian("librarian1", "EMP001"));
        }
        // Add a book if none exist
        if (bookDao.findAll().isEmpty()) {
            bookDao.addBook(new Book(0, "The Hobbit", "J.R.R. Tolkien", true, 1));
        }
    }
}
