package com.example.library.dao;

import com.example.library.model.Book;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for Book operations.
 */
public interface BookDao {

    /**
     * Adds a new book to the database.
     *
     * @param book The book to add.
     */
    void addBook(Book book);

    /**
     * Finds a book by its ID.
     *
     * @param bookId The ID of the book to find.
     * @return An Optional containing the book if found, or empty if not.
     */
    Optional<Book> findById(int bookId);

    /**
     * Retrieves all books from the database.
     *
     * @return A list of all books.
     */
    List<Book> findAll();

    /**
     * Updates an existing book's information.
     *
     * @param book The book with updated information.
     */
    void updateBook(Book book);

    /**
     * Deletes a book from the database.
     *
     * @param bookId The ID of the book to delete.
     */
    void deleteBook(int bookId);

    /**
     * Searches for books by title.
     *
     * @param title The title to search for.
     * @return A list of books with matching titles.
     */
    List<Book> findByTitle(String title);

    /**
     * Searches for books by author.
     *
     * @param author The author to search for.
     * @return A list of books with matching authors.
     */
    List<Book> findByAuthor(String author);

    /**
     * Finds all available books.
     *
     * @return A list of all available books.
     */
    List<Book> findAvailableBooks();
}
