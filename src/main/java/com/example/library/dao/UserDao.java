package com.example.library.dao;

import com.example.library.model.User;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for User operations.
 */
public interface UserDao {

    /**
     * Adds a new user to the database.
     *
     * @param user The user to add.
     */
    void addUser(User user);

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user to find.
     * @return An Optional containing the user if found, or empty if not.
     */
    Optional<User> findById(int userId);

    /**
     * Finds users by their name.
     *
     * @param name The name of the user to find.
     * @return A list of users with the given name.
     */
    List<User> findByName(String name);

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all users.
     */
    List<User> findAll();

    /**
     * Updates an existing user's information.
     *
     * @param user The user with updated information.
     */
    void updateUser(User user);

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete.
     */
    void deleteUser(int userId);
}
