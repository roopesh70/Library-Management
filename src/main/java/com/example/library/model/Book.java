package com.example.library.model;

import java.util.Objects;

/**
 * Represents a book in the library.
 */
public class Book {

    private int bookId;
    private String title;
    private String author;
    private boolean isAvailable;
    private int categoryId;

    /**
     * Default constructor.
     */
    public Book() {}

    /**
     * Constructs a Book with specified details.
     *
     * @param bookId      The ID of the book.
     * @param title       The title of the book.
     * @param author      The author of the book.
     * @param isAvailable The availability status of the book.
     * @param categoryId  The ID of the category this book belongs to.
     */
    public Book(int bookId, String title, String author, boolean isAvailable, int categoryId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
        this.categoryId = categoryId;
    }

    // Getters and Setters

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return bookId == book.bookId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    @Override
    public String toString() {
        return "Book{"
               + "bookId=" + bookId +
               ", title='" + title + "'"
               + ", author='" + author + "'"
               + ", isAvailable=" + isAvailable +
               ", categoryId=" + categoryId +
               '}';
    }
}
