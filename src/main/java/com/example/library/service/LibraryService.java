package com.example.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.library.dao.BookDao;
import com.example.library.dao.TransactionDao;
import com.example.library.interfaces.Searchable;
import com.example.library.model.Book;
import com.example.library.model.Student;
import com.example.library.model.Transaction;
import com.example.library.model.User;

/**
 * Core service for library operations.
 */
public class LibraryService implements Searchable {

    private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);
    private static final int BORROW_DURATION_DAYS = 14;

    private final BookDao bookDao;
    private final TransactionDao transactionDao;
    private final FineManager fineManager;
    private final NotificationService notificationService;

    public LibraryService(BookDao bookDao, TransactionDao transactionDao, FineManager fineManager, NotificationService notificationService) {
        this.bookDao = bookDao;
        this.transactionDao = transactionDao;
        this.fineManager = fineManager;
        this.notificationService = notificationService;
    }

    /**
     * Borrows a book for a user.
     *
     * @param user The user borrowing the book.
     * @param book The book to be borrowed.
     * @return true if the borrow operation is successful, false otherwise.
     */
    public boolean borrowBook(User user, Book book) {
        if (!book.isAvailable()) {
            logger.warn("Attempted to borrow an unavailable book: {}", book.getTitle());
            System.out.println("Sorry, this book is currently unavailable.");
            return false;
        }

        if (user instanceof Student) {
            long borrowedCount = transactionDao.findByUserId(user.getUserId()).stream()
                    .filter(t -> t.getReturnDate() == null)
                    .count();
            if (borrowedCount >= Student.BORROW_LIMIT) {
                logger.warn("Student {} has reached their borrow limit.", user.getName());
                System.out.println("You have reached your borrow limit of " + Student.BORROW_LIMIT + " books.");
                return false;
            }
        }

        // Create transaction
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROW_DURATION_DAYS);
        Transaction transaction = new Transaction(0, user.getUserId(), book.getBookId(), borrowDate, dueDate);
        transactionDao.addTransaction(transaction);

        // Mark book as unavailable
        book.setAvailable(false);
        bookDao.updateBook(book);

        logger.info("User {} borrowed book {}", user.getName(), book.getTitle());
        notificationService.sendReminder(user, "You have successfully borrowed '" + book.getTitle() + "'. Due date: " + dueDate);
        return true;
    }

    /**
     * Returns a book.
     *
     * @param book The book to be returned.
     * @return true if the return operation is successful, false otherwise.
     */
    public boolean returnBook(Book book) {
        AtomicBoolean success = new AtomicBoolean(false);
        transactionDao.findActiveTransactionByBookId(book.getBookId()).ifPresentOrElse(transaction -> {
            // Calculate fine
            transaction.setReturnDate(LocalDate.now());
            double fine = fineManager.calculateFine(transaction);
            transaction.setFineAmount(fine);
            transactionDao.updateTransaction(transaction);

            // Mark book as available
            book.setAvailable(true);
            bookDao.updateBook(book);

            logger.info("Book {} returned.", book.getTitle());
            if (fine > 0) {
                System.out.printf("Book returned successfully. A fine of $%.2f has been applied for the overdue return.%n", fine);
            } else {
                System.out.println("Book returned successfully.");
            }
            success.set(true);
        }, () -> {
            logger.warn("No active transaction found for book {}", book.getTitle());
            System.out.println("Could not process return: No active borrow record found for this book.");
            success.set(false);
        });
        return success.get();
    }

    @Override
    public List<Book> searchByTitle(String title) {
        return bookDao.findByTitle(title);
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        return bookDao.findByAuthor(author);
    }

    /**
     * Lists all available books.
     *
     * @return A list of all available books.
     */
    public List<Book> listAvailableBooks() {
        return bookDao.findAvailableBooks();
    }

    /**
     * Lists all books borrowed by a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of books currently borrowed by the user.
     */
    public List<Book> getBorrowedBooks(int userId) {
        return transactionDao.findByUserId(userId).stream()
                .filter(t -> t.getReturnDate() == null)
                .map(t -> bookDao.findById(t.getBookId()))
                .flatMap(java.util.Optional::stream)
                .collect(java.util.stream.Collectors.toList());
    }
}
