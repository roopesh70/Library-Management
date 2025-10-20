package com.example.library;

import com.example.library.model.Transaction;
import com.example.library.service.FineManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

public class FineManagerTest {

    private FineManager fineManager;

    @BeforeEach
    void setUp() {
        fineManager = new FineManager(0.50); // $0.50 per day
    }

    @Test
    void testCalculateFine_NoOverdue() {
        Transaction transaction = new Transaction();
        transaction.setDueDate(LocalDate.now().minusDays(5));
        transaction.setReturnDate(LocalDate.now().minusDays(6)); // Returned before due date
        assertEquals(0, fineManager.calculateFine(transaction));
    }

    @Test
    void testCalculateFine_OnTime() {
        Transaction transaction = new Transaction();
        LocalDate date = LocalDate.now();
        transaction.setDueDate(date);
        transaction.setReturnDate(date); // Returned on the due date
        assertEquals(0, fineManager.calculateFine(transaction));
    }

    @Test
    void testCalculateFine_ThreeDaysOverdue() {
        Transaction transaction = new Transaction();
        transaction.setDueDate(LocalDate.now().minusDays(3));
        transaction.setReturnDate(LocalDate.now()); // Returned 3 days late
        assertEquals(1.50, fineManager.calculateFine(transaction));
    }

    @Test
    void testCalculateFine_NullReturnDate() {
        Transaction transaction = new Transaction();
        transaction.setDueDate(LocalDate.now().minusDays(1));
        // Return date is null
        assertEquals(0, fineManager.calculateFine(transaction));
    }
}
