package com.example.library.dao;

import com.example.library.db.DatabaseManager;
import com.example.library.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTransactionDao implements TransactionDao {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionDao.class);
    private final DatabaseManager databaseManager;

    public JdbcTransactionDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, book_id, borrow_date, due_date, return_date, fine_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, transaction.getUserId());
            pstmt.setInt(2, transaction.getBookId());
            pstmt.setDate(3, Date.valueOf(transaction.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(transaction.getDueDate()));
            pstmt.setDate(5, transaction.getReturnDate() != null ? Date.valueOf(transaction.getReturnDate()) : null);
            pstmt.setDouble(6, transaction.getFineAmount());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                }
            }
            logger.info("Added transaction: {}", transaction);
        } catch (SQLException e) {
            logger.error("Error adding transaction", e);
        }
    }

    @Override
    public Optional<Transaction> findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding transaction by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions by user ID", e);
        }
        return transactions;
    }

    @Override
    public Optional<Transaction> findActiveTransactionByBookId(int bookId) {
        String sql = "SELECT * FROM transactions WHERE book_id = ? AND return_date IS NULL";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding active transaction by book ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all transactions", e);
        }
        return transactions;
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        String sql = "UPDATE transactions SET user_id = ?, book_id = ?, borrow_date = ?, due_date = ?, return_date = ?, fine_amount = ? WHERE transaction_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaction.getUserId());
            pstmt.setInt(2, transaction.getBookId());
            pstmt.setDate(3, Date.valueOf(transaction.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(transaction.getDueDate()));
            pstmt.setDate(5, transaction.getReturnDate() != null ? Date.valueOf(transaction.getReturnDate()) : null);
            pstmt.setDouble(6, transaction.getFineAmount());
            pstmt.setInt(7, transaction.getTransactionId());
            pstmt.executeUpdate();
            logger.info("Updated transaction: {}", transaction);
        } catch (SQLException e) {
            logger.error("Error updating transaction", e);
        }
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setBookId(rs.getInt("book_id"));
        transaction.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        transaction.setDueDate(rs.getDate("due_date").toLocalDate());
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            transaction.setReturnDate(returnDate.toLocalDate());
        }
        transaction.setFineAmount(rs.getDouble("fine_amount"));
        return transaction;
    }
}
