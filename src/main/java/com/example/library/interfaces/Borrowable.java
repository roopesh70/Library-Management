package com.example.library.interfaces;

import com.example.library.model.Book;

/**
 * Interface for objects that can be borrowed and returned.
 */
public interface Borrowable {

    /**
     * Borrows a book.
     *
     * @param book The book to be borrowed.
     */
    void borrowBook(Book book);

    /**
     * Returns a book.
     *
     * @param book The book to be returned.
     */
    void returnBook(Book book);
}
