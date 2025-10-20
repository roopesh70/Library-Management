package com.example.library.service;

import com.example.library.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for sending notifications to users.
 */
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Sends a reminder to a user.
     * In a real application, this would send an email or SMS.
     *
     * @param user The user to send the reminder to.
     */
    public void sendReminder(User user, String message) {
        // For now, we just print to the console.
        String notification = String.format("Notification for %s: %s", user.getName(), message);
        System.out.println(notification);
        logger.info("Sent notification to user {}: {}", user.getUserId(), message);
    }
}
