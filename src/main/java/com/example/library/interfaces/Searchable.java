package com.example.library.interfaces;

import com.example.library.model.Book;
import java.util.List;

/**
 * Interface for search operations.
 */
public interface Searchable {

    /**
     * Searches for books by title.
     *
     * @param title The title to search for.
     * @return A list of books matching the title.
     */
    List<Book> searchByTitle(String title);

    /**
     * Searches for books by author.
     *
     * @param author The author to search for.
     * @return A list of books matching the author.
     */
    List<Book> searchByAuthor(String author);
}
