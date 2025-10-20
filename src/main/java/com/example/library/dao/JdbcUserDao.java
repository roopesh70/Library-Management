package com.example.library.dao;

import com.example.library.db.DatabaseManager;
import com.example.library.model.Librarian;
import com.example.library.model.Student;
import com.example.library.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUserDao.class);
    private final DatabaseManager databaseManager;

    public JdbcUserDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (name, user_type, department, year_of_study, employee_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            if (user instanceof Student) {
                Student student = (Student) user;
                pstmt.setString(2, "STUDENT");
                pstmt.setString(3, student.getDepartment());
                pstmt.setInt(4, student.getYearOfStudy());
                pstmt.setNull(5, Types.VARCHAR);
            } else if (user instanceof Librarian) {
                Librarian librarian = (Librarian) user;
                pstmt.setString(2, "LIBRARIAN");
                pstmt.setNull(3, Types.VARCHAR);
                pstmt.setNull(4, Types.INTEGER);
                pstmt.setString(5, librarian.getEmployeeId());
            }
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }
            logger.info("Added user: {}", user);
        } catch (SQLException e) {
            logger.error("Error adding user", e);
        }
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by name", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        }
        return users;
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET name = ?, department = ?, year_of_study = ?, employee_id = ? WHERE user_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            if (user instanceof Student) {
                Student student = (Student) user;
                pstmt.setString(2, student.getDepartment());
                pstmt.setInt(3, student.getYearOfStudy());
                pstmt.setNull(4, Types.VARCHAR);
            } else if (user instanceof Librarian) {
                Librarian librarian = (Librarian) user;
                pstmt.setNull(2, Types.VARCHAR);
                pstmt.setNull(3, Types.INTEGER);
                pstmt.setString(4, librarian.getEmployeeId());
            }
            pstmt.setInt(5, user.getUserId());
            pstmt.executeUpdate();
            logger.info("Updated user: {}", user);
        } catch (SQLException e) {
            logger.error("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            logger.info("Deleted user with ID: {}", userId);
        } catch (SQLException e) {
            logger.error("Error deleting user", e);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String name = rs.getString("name");
        String userType = rs.getString("user_type");

        User user;
        if ("STUDENT".equals(userType)) {
            String department = rs.getString("department");
            int yearOfStudy = rs.getInt("year_of_study");
            user = new Student(name, department, yearOfStudy);
        } else {
            String employeeId = rs.getString("employee_id");
            user = new Librarian(name, employeeId);
        }
        user.setUserId(userId);
        return user;
    }
}
