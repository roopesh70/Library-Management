package com.example.library.cli;

import com.example.library.dao.BookDao;
import com.example.library.model.Book;
import com.example.library.model.Librarian;
import com.example.library.model.User;
import com.example.library.service.AuthenticationService;
import com.example.library.service.LibraryService;
import com.example.library.service.ReportGenerator;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner;
    private final AuthenticationService authService;
    private final LibraryService libraryService;
    private final ReportGenerator reportGenerator;
    private final BookDao bookDao; // For adding/removing books

    public MainMenu(Scanner scanner, AuthenticationService authService, LibraryService libraryService, ReportGenerator reportGenerator, BookDao bookDao) {
        this.scanner = scanner;
        this.authService = authService;
        this.libraryService = libraryService;
        this.reportGenerator = reportGenerator;
        this.bookDao = bookDao;
    }

    public void run() {
        while (true) {
            if (!authService.isUserLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n--- Welcome to the Library Management System ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            handleLogin();
        } else if (choice == 2) {
            System.out.println("Exiting...");
            System.exit(0);
        }
    }

    private void showMainMenu() {
        User currentUser = authService.getCurrentUser();
        System.out.printf("\n--- Main Menu (Logged in as: %s) ---\%n", currentUser.getName());
        System.out.println("1. Search Book");
        System.out.println("2. Borrow Book");
        System.out.println("3. Return Book");
        System.out.println("4. View My Borrowed Books");

        if (currentUser instanceof Librarian) {
            System.out.println("5. Add Book");
            System.out.println("6. Remove Book");
            System.out.println("7. Generate Reports");
        }

        System.out.println("9. Logout");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: handleSearchBook(); break;
            case 2: handleBorrowBook(); break;
            case 3: handleReturnBook(); break;
            case 4: handleViewMyBorrowedBooks(); break;
            case 9: authService.logout(); break;
            default:
                if (currentUser instanceof Librarian) {
                    switch (choice) {
                        case 5: handleAddBook(); break;
                        case 6: handleRemoveBook(); break;
                        case 7: handleGenerateReports(); break;
                        default: System.out.println("Invalid option.");
                    }
                } else {
                    System.out.println("Invalid option.");
                }
        }
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine(); // Password not validated in this version

        if (!authService.login(username, password)) {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    private void handleSearchBook() {
        System.out.print("Search by (1) Title or (2) Author: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        List<Book> books;
        if (choice == 1) {
            System.out.print("Enter title: ");
            String title = scanner.nextLine();
            books = libraryService.searchBookByTitle(title);
        } else {
            System.out.print("Enter author: ");
            String author = scanner.nextLine();
            books = libraryService.searchBookByAuthor(author);
        }
        
        System.out.println("\n--- Search Results ---");
        if (books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            books.forEach(b -> System.out.printf("ID: %d, Title: %s, Author: %s, Available: %s%n", 
                b.getBookId(), b.getTitle(), b.getAuthor(), b.isAvailable()));
        }
    }

    private void handleBorrowBook() {
        System.out.print("Enter the ID of the book to borrow: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();
        bookDao.findById(bookId).ifPresentOrElse(
            book -> libraryService.borrowBook(authService.getCurrentUser(), book),
            () -> System.out.println("Book with ID " + bookId + " not found.")
        );
    }

    private void handleReturnBook() {
        System.out.print("Enter the ID of the book to return: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();
        bookDao.findById(bookId).ifPresentOrElse(
            libraryService::returnBook,
            () -> System.out.println("Book with ID " + bookId + " not found.")
        );
    }

    private void handleViewMyBorrowedBooks() {
        List<Book> borrowedBooks = libraryService.getBorrowedBooks(authService.getCurrentUser().getUserId());
        System.out.println("\n--- My Borrowed Books ---");
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have not borrowed any books.");
        } else {
            borrowedBooks.forEach(b -> System.out.printf("ID: %d, Title: %s, Author: %s%n", 
                b.getBookId(), b.getTitle(), b.getAuthor()));
        }
    }

    // --- Librarian-only methods ---

    private void handleAddBook() {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter category ID: ");
        int categoryId = scanner.nextInt();
        scanner.nextLine();

        Book newBook = new Book(0, title, author, true, categoryId);
        bookDao.addBook(newBook);
        System.out.println("Book added successfully!");
    }

    private void handleRemoveBook() {
        System.out.print("Enter the ID of the book to remove: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();
        bookDao.deleteBook(bookId);
        System.out.println("Book removed successfully!");
    }

    private void handleGenerateReports() {
        System.out.println("\n--- Generate Reports ---");
        System.out.println("1. Most Borrowed Books");
        System.out.println("2. Overdue Users");
        System.out.print("Choose a report: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            reportGenerator.generateMostBorrowedBooksReport();
        } else if (choice == 2) {
            reportGenerator.generateOverdueUsersReport();
        }
    }
}
