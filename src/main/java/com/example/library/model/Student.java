package com.example.library.model;

/**
 * Represents a student user.
 */
public class Student extends User {

    public static final int BORROW_LIMIT = 5;

    private String department;
    private int yearOfStudy;

    /**
     * Constructs a Student with specified details.
     *
     * @param name        The name of the student.
     * @param department  The department of the student.
     * @param yearOfStudy The year of study of the student.
     */
    public Student(String name, String department, int yearOfStudy) {
        super(name);
        this.department = department;
        this.yearOfStudy = yearOfStudy;
    }

    // Getters and Setters

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public String toString() {
        return "Student{"
               + "userId=" + userId
               + ", name='" + name + "'"
               + ", department='" + department + "'"
               + ", yearOfStudy=" + yearOfStudy
               + "'";
    }
}
