package com.example.library;

import com.example.library.dao.*;
import com.example.library.db.DatabaseManager;
import com.example.library.model.Book;
import com.example.library.model.Student;
import com.example.library.service.FineManager;
import com.example.library.service.LibraryService;
import com.example.library.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BorrowReturnIntegrationTest {

    private LibraryService libraryService;
    private BookDao bookDao;
    private UserDao userDao;
    private Student testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Use H2 in-memory database for testing
        DatabaseManager dbManager = new DatabaseManager();
        bookDao = new JdbcBookDao(dbManager);
        userDao = new JdbcUserDao(dbManager);
        TransactionDao transactionDao = new JdbcTransactionDao(dbManager);

        // Services
        FineManager fineManager = new FineManager(0.50);
        NotificationService notificationService = new NotificationService(); // Mock or simple version
        libraryService = new LibraryService(bookDao, transactionDao, fineManager, notificationService);

        // Test data
        testUser = new Student("integration_tester", "Test Dept", 1);
        userDao.addUser(testUser);

        testBook = new Book(0, "Integration Test Book", "Tester", true, 1);
        bookDao.addBook(testBook);
    }

    @Test
    void testBorrowAndReturnFlow() {
        // 1. Borrow the book
        boolean borrowSuccess = libraryService.borrowBook(testUser, testBook);
        assertTrue(borrowSuccess, "Borrow operation should be successful.");

        // 2. Verify book is unavailable
        Book borrowedBook = bookDao.findById(testBook.getBookId()).orElse(null);
        assertNotNull(borrowedBook);
        assertFalse(borrowedBook.isAvailable(), "Book should be marked as unavailable after borrowing.");

        // 3. Verify user has the book
        long borrowedCount = libraryService.getBorrowedBooks(testUser.getUserId()).stream()
            .filter(b -> b.getBookId() == testBook.getBookId())
            .count();
        assertEquals(1, borrowedCount, "User should have one borrowed book.");

        // 4. Return the book
        boolean returnSuccess = libraryService.returnBook(testBook);
        assertTrue(returnSuccess, "Return operation should be successful.");

        // 5. Verify book is available again
        Book returnedBook = bookDao.findById(testBook.getBookId()).orElse(null);
        assertNotNull(returnedBook);
        assertTrue(returnedBook.isAvailable(), "Book should be marked as available after returning.");

        // 6. Verify user no longer has the book
        borrowedCount = libraryService.getBorrowedBooks(testUser.getUserId()).stream()
            .filter(b -> b.getBookId() == testBook.getBookId())
            .count();
        assertEquals(0, borrowedCount, "User should have no borrowed books after returning.");
    }
}
