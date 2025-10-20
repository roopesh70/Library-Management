package com.example.library.model;

import java.util.Objects;

/**
 * Represents a book category.
 */
public class Category {

    private int categoryId;
    private String name;
    private String description;

    /**
     * Default constructor.
     */
    public Category() {}

    /**
     * Constructs a Category with specified details.
     *
     * @param categoryId  The ID of the category.
     * @param name        The name of the category.
     * @param description A description of the category.
     */
    public Category(int categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryId == category.categoryId && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, name);
    }

    @Override
    public String toString() {
        return "Category{"
               + "categoryId=" + categoryId + ", "
               + "name='" + name + "'" + ", "
               + "description='" + description + "'" + 
               '}';
    }
}
