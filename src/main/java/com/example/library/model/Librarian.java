package com.example.library.model;

/**
 * Represents a librarian user.
 */
public class Librarian extends User {

    private String employeeId;

    /**
     * Constructs a Librarian with specified details.
     *
     * @param name       The name of the librarian.
     * @param employeeId The employee ID of the librarian.
     */
    public Librarian(String name, String employeeId) {
        super(name);
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty.");
        }
        this.employeeId = employeeId;
    }

    // Getters and Setters

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Librarian{"
               + "userId=" + userId +
               ", name='" + name + "'" +
               ", employeeId='" + employeeId + "'" +
               '}';
    }
}
