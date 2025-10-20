package com.example.library.service;

import com.example.library.model.Transaction;
import java.time.temporal.ChronoUnit;

/**
 * Manages fine calculations for overdue books.
 */
public class FineManager {

    private final double fineRatePerDay;

    /**
     * Constructs a FineManager with a specified fine rate.
     *
     * @param fineRatePerDay The fine rate per day for overdue books.
     */
    public FineManager(double fineRatePerDay) {
        this.fineRatePerDay = fineRatePerDay;
    }

    /**
     * Calculates the fine for a given transaction.
     *
     * @param t The transaction to calculate the fine for.
     * @return The calculated fine amount, or 0 if not overdue.
     */
    public double calculateFine(Transaction t) {
        if (t.getReturnDate() == null || t.getDueDate() == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(t.getDueDate(), t.getReturnDate());
        return days > 0 ? days * fineRatePerDay : 0;
    }
}
