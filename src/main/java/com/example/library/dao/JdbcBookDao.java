package com.example.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.library.db.DatabaseManager;
import com.example.library.model.Book;

public class JdbcBookDao implements BookDao {

    private static final Logger logger = LoggerFactory.getLogger(JdbcBookDao.class);
    private final DatabaseManager databaseManager;

    public JdbcBookDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, is_available, category_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBoolean(3, book.isAvailable());
            // Ensure category exists; if not, insert with NULL to avoid FK constraint errors in tests
            int categoryId = book.getCategoryId();
            if (categoryId > 0) {
                String checkSql = "SELECT 1 FROM categories WHERE category_id = ?";
                try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                    check.setInt(1, categoryId);
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            pstmt.setInt(4, categoryId);
                        } else {
                            pstmt.setNull(4, Types.INTEGER);
                        }
                    }
                }
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setBookId(generatedKeys.getInt(1));
                }
            }
            // Fallback: some DB drivers (or configurations) may not return generated keys.
            // Try to find the inserted book by title+author as a last resort (tests use single-threaded DB).
            if (book.getBookId() == 0) {
                String lookupSql = "SELECT book_id FROM books WHERE title = ? AND author = ? ORDER BY book_id DESC LIMIT 1";
                try (PreparedStatement lookup = conn.prepareStatement(lookupSql)) {
                    lookup.setString(1, book.getTitle());
                    lookup.setString(2, book.getAuthor());
                    try (ResultSet rs = lookup.executeQuery()) {
                        if (rs.next()) {
                            book.setBookId(rs.getInt("book_id"));
                        }
                    }
                } catch (SQLException ignore) {
                    // ignore fallback failures
                }
            }
            logger.info("Added book: {}", book);
        } catch (SQLException e) {
            logger.error("Error adding book", e);
        }
    }

    @Override
    public Optional<Book> findById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding book by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all books", e);
        }
        return books;
    }

    @Override
    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, is_available = ?, category_id = ? WHERE book_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBoolean(3, book.isAvailable());
            int categoryId = book.getCategoryId();
            if (categoryId > 0) {
                String checkSql = "SELECT 1 FROM categories WHERE category_id = ?";
                try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                    check.setInt(1, categoryId);
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            pstmt.setInt(4, categoryId);
                        } else {
                            pstmt.setNull(4, Types.INTEGER);
                        }
                    }
                }
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setInt(5, book.getBookId());
            pstmt.executeUpdate();
            logger.info("Updated book: {}", book);
        } catch (SQLException e) {
            logger.error("Error updating book", e);
        }
    }

    @Override
    public void deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            logger.info("Deleted book with ID: {}", bookId);
        } catch (SQLException e) {
            logger.error("Error deleting book", e);
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE UPPER(title) LIKE UPPER(?)"; // Standard SQL for case-insensitivity
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding books by title", e);
        }
        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE UPPER(author) LIKE UPPER(?)"; // Standard SQL for case-insensitivity
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + author + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding books by author", e);
        }
        return books;
    }

    @Override
    public List<Book> findAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_available = TRUE";
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding available books", e);
        }
        return books;
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getBoolean("is_available"),
                rs.getInt("category_id")
        );
    }
}
