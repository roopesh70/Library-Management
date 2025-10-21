package com.example.library.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.library.dao.UserDao;
import com.example.library.model.User;

/**
 * Service for user authentication.
 */
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserDao userDao;
    private User currentUser;

    public AuthenticationService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Logs in a user.
     *
     * @param username The username of the user.
     * @param password The password (not used in this mock implementation).
     * @return true if login is successful, false otherwise.
     */
    public boolean login(String username, String password) {
        // This is a mock authentication. In a real app, you'd validate the password.
        List<User> users = userDao.findByName(username);
        if (!users.isEmpty()) {
            // If multiple users have the same name, this mock implementation logs in the first one found.
            this.currentUser = users.get(0);
            logger.info("User '{}' logged in successfully.", username);
            return true;
        }
        logger.warn("Login failed for user '{}'.", username);
        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User '{}' logged out.", currentUser.getName());
            this.currentUser = null;
        }
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The current user, or null if no one is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
}
