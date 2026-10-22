package com.library.lms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "issues")
public class Issue {

    public static final int FINE_PER_DAY = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int fine;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public int getFine() { return fine; }
    public void setFine(int fine) { this.fine = fine; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // not stored in db, only used on the pages
    public boolean isOverdue() {
        return returnDate == null && dueDate.isBefore(LocalDate.now());
    }

    public int getLiveFine() {
        if (returnDate != null) {
            return fine;
        }
        long lateDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return lateDays > 0 ? (int) lateDays * FINE_PER_DAY : 0;
    }
}
