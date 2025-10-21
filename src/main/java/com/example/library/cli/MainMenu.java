package com.example.library.cli;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.example.library.dao.BookDao;
import com.example.library.dao.UserDao;
import com.example.library.model.Book;
import com.example.library.model.Librarian;
import com.example.library.model.Student;
import com.example.library.model.User;
import com.example.library.service.AuthenticationService;
import com.example.library.service.LibraryService;
import com.example.library.service.ReportGenerator;

public class MainMenu {

    private final Scanner scanner;
    private final AuthenticationService authService;
    private final LibraryService libraryService;
    private final ReportGenerator reportGenerator;
    private final BookDao bookDao;
    private final UserDao userDao; 

    public MainMenu(Scanner scanner, AuthenticationService authService,
                    LibraryService libraryService, ReportGenerator reportGenerator,
                    BookDao bookDao, UserDao userDao) {
        this.scanner = scanner;
        this.authService = authService;
        this.libraryService = libraryService;
        this.reportGenerator = reportGenerator;
        this.bookDao = bookDao;
        this.userDao = userDao;
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

    /**
     * Reads an integer from scanner with basic validation. Returns -1 on invalid input.
     */
    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int v = scanner.nextInt();
            scanner.nextLine(); // consume newline
            return v;
        } catch (java.util.InputMismatchException ime) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // consume invalid token
            return -1;
        }
    }

    private void showLoginMenu() {
        System.out.println("\n--- Welcome to the Library Management System ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        int choice = readInt("Choose an option: ");
        if (choice == -1) return;

        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> {
                System.out.println("Exiting...");
                System.exit(0);
            }
            default -> System.out.println("Invalid option.");
        }
    }

    private void showMainMenu() {
        User currentUser = authService.getCurrentUser();
        System.out.printf("\n--- Main Menu (Logged in as: %s) --- %n", currentUser.getName());
        System.out.println("1. Search Book");
        System.out.println("2. Borrow Book");
        System.out.println("3. Return Book");
        System.out.println("4. View My Borrowed Books");
        

        if (currentUser instanceof Librarian) {
            System.out.println("5. Add Book");
            System.out.println("6. Remove Book");
            System.out.println("7. Generate Reports");
            System.out.println("8. Add User");
        }

        System.out.println("9. Logout");
        int choice = readInt("Choose an option: ");
        if (choice == -1) return;

        switch (choice) {
            case 1 -> handleSearchBook();
            case 2 -> handleBorrowBook();
            case 3 -> handleReturnBook();
            case 4 -> handleViewMyBorrowedBooks();
            case 9 -> authService.logout();
            case 5, 6, 7, 8 -> {
                if (currentUser instanceof Librarian) {
                    switch (choice) {
                        case 5 -> handleAddBook();
                        case 6 -> handleRemoveBook();
                        case 7 -> handleGenerateReports();
                        case 8 -> handleAddUser();
                    }
                } else {
                    System.out.println("Invalid option.");
                }
            }
            default -> System.out.println("Invalid option.");
        }
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (!authService.login(username, password)) {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    private void handleSearchBook() {
        int choice = readInt("Search by (1) Title or (2) Author: ");
        if (choice == -1) return;
        List<Book> books;

        if (choice == 1) {
            System.out.print("Enter title: ");
            String title = scanner.nextLine();
            books = libraryService.searchByTitle(title);
        } else {
            System.out.print("Enter author: ");
            String author = scanner.nextLine();
            books = libraryService.searchByAuthor(author);
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
    int bookId = readInt("Enter the ID of the book to borrow: ");
    if (bookId == -1) return;

        Optional<Book> bookOpt = bookDao.findById(bookId);
        if (bookOpt.isPresent()) {
            libraryService.borrowBook(authService.getCurrentUser(), bookOpt.get());
        } else {
            System.out.println("Book with ID " + bookId + " not found.");
        }
    }

    private void handleReturnBook() {
    int bookId = readInt("Enter the ID of the book to return: ");
    if (bookId == -1) return;

        Optional<Book> bookOpt = bookDao.findById(bookId);
        if (bookOpt.isPresent()) {
            libraryService.returnBook(bookOpt.get());
        } else {
            System.out.println("Book with ID " + bookId + " not found.");
        }
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

    private void handleAddBook() {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
    int categoryId = readInt("Enter category ID: ");
    if (categoryId == -1) return;

        Book newBook = new Book(0, title, author, true, categoryId);
        bookDao.addBook(newBook);
        System.out.println("Book added successfully!");
    }

    private void handleRemoveBook() {
    int bookId = readInt("Enter the ID of the book to remove: ");
    if (bookId == -1) return;
        bookDao.deleteBook(bookId);
        System.out.println("Book removed successfully!");
    }

    private void handleGenerateReports() {
        System.out.println("\n--- Generate Reports ---");
        System.out.println("1. Most Borrowed Books");
        System.out.println("2. Overdue Users");
    int choice = readInt("Choose a report: ");
    if (choice == -1) return;

        switch (choice) {
            case 1 -> reportGenerator.generateMostBorrowedBooksReport();
            case 2 -> reportGenerator.generateOverdueUsersReport();
            default -> System.out.println("Invalid option.");
        }
    }

    private void handleAddUser() {
    System.out.println("\n--- Add New User ---");
    System.out.print("Enter user type (1 for Student, 2 for Librarian): ");
    int userTypeChoice;
    try {
        userTypeChoice = scanner.nextInt();
    } catch (java.util.InputMismatchException e) {
        System.out.println("Invalid input. Please enter a number.");
        scanner.nextLine(); // Consume the rest of the invalid line
        return;
    }
    scanner.nextLine(); // Consume the newline character after the number

    System.out.print("Enter username: ");
    String username = scanner.nextLine();

    switch (userTypeChoice) {
        case 1: // Student
            System.out.print("Enter department: ");
            String department = scanner.nextLine();
            System.out.print("Enter year of study: ");
            int year;
            try {
                year = scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid year. Please enter a number.");
                scanner.nextLine(); // Consume the rest of the invalid line
                return;
            }
            scanner.nextLine(); // Consume newline

            userDao.addUser(new Student(username, department, year));
            System.out.println("Student added successfully!");
            break;

        case 2: // Librarian
            System.out.print("Enter employee ID: ");
            String employeeId = scanner.nextLine();
            userDao.addUser(new Librarian(username, employeeId));
            System.out.println("Librarian added successfully!");
            break;

        default:
            System.out.println("Invalid user type. User not added.");
            break;
    }
}
}
