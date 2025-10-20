package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a borrowing transaction.
 */
public class Transaction {

    private int transactionId;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;

    /**
     * Default constructor.
     */
    public Transaction() {}

    /**
     * Constructs a Transaction with specified details.
     *
     * @param transactionId The ID of the transaction.
     * @param userId        The ID of the user.
     * @param bookId        The ID of the book.
     * @param borrowDate    The date the book was borrowed.
     * @param dueDate       The date the book is due.
     */
    public Transaction(int transactionId, int userId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    // Getters and Setters

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "transactionId=" + transactionId +
               ", userId=" + userId +
               ", bookId=" + bookId +
               ", borrowDate=" + borrowDate +
               ", dueDate=" + dueDate +
               ", returnDate=" + returnDate +
               ", fineAmount=" + fineAmount +
               '}';
    }
}
