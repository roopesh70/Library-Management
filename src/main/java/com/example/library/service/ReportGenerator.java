package com.example.library.service;

import com.example.library.dao.BookDao;
import com.example.library.dao.TransactionDao;
import com.example.library.dao.UserDao;
import com.example.library.model.Book;
import com.example.library.model.Transaction;
import com.example.library.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating various reports.
 */
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    private final BookDao bookDao;
    private final UserDao userDao;
    private final TransactionDao transactionDao;

    public ReportGenerator(BookDao bookDao, UserDao userDao, TransactionDao transactionDao) {
        this.bookDao = bookDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
    }

    /**
     * Generates a report of the most borrowed books.
     */
    public void generateMostBorrowedBooksReport() {
        logger.info("Generating 'Most Borrowed Books' report...");
        List<Transaction> transactions = transactionDao.findAll();
        Map<Integer, Long> bookBorrowCounts = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getBookId, Collectors.counting()));

        System.out.println("\n--- Most Borrowed Books Report ---");
        bookBorrowCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    bookDao.findById(entry.getKey())
                            .ifPresent(book -> 
                                System.out.printf("'%s' by %s - Borrowed %d times%n", 
                                book.getTitle(), book.getAuthor(), entry.getValue()));
                });
        System.out.println("------------------------------------\n");
    }

    /**
     * Generates a report of users with overdue books.
     */
    public void generateOverdueUsersReport() {
        logger.info("Generating 'Overdue Users' report...");
        List<Transaction> transactions = transactionDao.findAll();
        LocalDate today = LocalDate.now();

        System.out.println("\n--- Overdue Users Report ---");
        transactions.stream()
                .filter(t -> t.getReturnDate() == null && t.getDueDate().isBefore(today))
                .forEach(t -> {
                    userDao.findById(t.getUserId()).ifPresent(user -> {
                        bookDao.findById(t.getBookId()).ifPresent(book -> {
                            System.out.printf("User: %s, Book: '%s', Due Date: %s%n", 
                            user.getName(), book.getTitle(), t.getDueDate());
                        });
                    });
                });
        System.out.println("----------------------------\n");
    }
}
