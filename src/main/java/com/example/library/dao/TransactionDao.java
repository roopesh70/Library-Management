package com.example.library.dao;

import com.example.library.model.Transaction;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for Transaction operations.
 */
public interface TransactionDao {

    /**
     * Adds a new transaction to the database.
     *
     * @param transaction The transaction to add.
     */
    void addTransaction(Transaction transaction);

    /**
     * Finds a transaction by its ID.
     *
     * @param transactionId The ID of the transaction to find.
     * @return An Optional containing the transaction if found, or empty if not.
     */
    Optional<Transaction> findById(int transactionId);

    /**
     * Finds all transactions for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of transactions for the given user.
     */
    List<Transaction> findByUserId(int userId);

    /**
     * Finds the active transaction for a specific book.
     *
     * @param bookId The ID of the book.
     * @return An Optional containing the active transaction if found, or empty if not.
     */
    Optional<Transaction> findActiveTransactionByBookId(int bookId);

    /**
     * Retrieves all transactions from the database.
     *
     * @return A list of all transactions.
     */
    List<Transaction> findAll();

    /**
     * Updates an existing transaction's information.
     *
     * @param transaction The transaction with updated information.
     */
    void updateTransaction(Transaction transaction);
}
