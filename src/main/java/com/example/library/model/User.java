package com.example.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an abstract user of the library.
 */
public abstract class User {

    protected int userId;
    protected String name;
    protected List<Integer> borrowedBookIds;

    /**
     * Constructs a User with a name.
     *
     * @param name The name of the user.
     */
    public User(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
        this.borrowedBookIds = new ArrayList<>();
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getBorrowedBookIds() {
        return borrowedBookIds;
    }

    public void setBorrowedBookIds(List<Integer> borrowedBookIds) {
        this.borrowedBookIds = borrowedBookIds;
    }

    /**
     * Adds a book to the user's borrowed list.
     * @param bookId The ID of the book to add.
     */
    public void borrowBook(int bookId) {
        this.borrowedBookIds.add(bookId);
    }

    /**
     * Removes a book from the user's borrowed list.
     * @param bookId The ID of the book to remove.
     */
    public void returnBook(int bookId) {
        this.borrowedBookIds.remove(Integer.valueOf(bookId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", name='" + name + "'" +
               '}';
    }
}
