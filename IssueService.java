package com.library.lms.service;

import com.library.lms.model.Book;
import com.library.lms.model.Issue;
import com.library.lms.model.User;
import com.library.lms.repository.BookRepository;
import com.library.lms.repository.IssueRepository;
import com.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class IssueService {

    public static final int LOAN_DAYS = 14;
    public static final int MAX_ACTIVE_BOOKS = 3;

    private final IssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public IssueService(IssueRepository issueRepository, BookRepository bookRepository,
                        UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Issue> findByFilter(String filter) {
        if ("active".equals(filter)) {
            return issueRepository.findByStatusOrderByDueDateAsc("ISSUED");
        }
        if ("overdue".equals(filter)) {
            return issueRepository.findByStatusAndDueDateBeforeOrderByDueDateAsc("ISSUED", LocalDate.now());
        }
        return issueRepository.findAllByOrderByIssueDateDescIdDesc();
    }

    public List<Issue> findByUser(User user) {
        return issueRepository.findByUserOrderByIssueDateDescIdDesc(user);
    }

    @Transactional
    public Issue issueBook(Long bookId, Long userId) {
        Book book = bookRepository.findForUpdate(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found"));
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Member not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copy of '" + book.getTitle() + "' is available right now");
        }
        if (issueRepository.existsByBookAndUserAndStatus(book, member, "ISSUED")) {
            throw new IllegalStateException(member.getName() + " already has a copy of this book");
        }
        if (issueRepository.countByUserAndStatus(member, "ISSUED") >= MAX_ACTIVE_BOOKS) {
            throw new IllegalStateException(
                    member.getName() + " already has " + MAX_ACTIVE_BOOKS + " books issued");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        Issue issue = new Issue();
        issue.setBook(book);
        issue.setUser(member);
        issue.setIssueDate(today);
        issue.setDueDate(today.plusDays(LOAN_DAYS));
        issue.setFine(0);
        issue.setStatus("ISSUED");
        return issueRepository.save(issue);
    }

    @Transactional
    public Issue returnBook(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalStateException("Issue record not found"));
        if ("RETURNED".equals(issue.getStatus())) {
            throw new IllegalStateException("This book is already returned");
        }

        Book book = bookRepository.findForUpdate(issue.getBook().getId())
                .orElseThrow(() -> new IllegalStateException("Book not found"));

        LocalDate today = LocalDate.now();
        long lateDays = ChronoUnit.DAYS.between(issue.getDueDate(), today);

        issue.setReturnDate(today);
        issue.setFine(lateDays > 0 ? (int) lateDays * Issue.FINE_PER_DAY : 0);
        issue.setStatus("RETURNED");

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return issueRepository.save(issue);
    }
}
