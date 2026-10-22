package com.library.lms.repository;

import com.library.lms.model.Book;
import com.library.lms.model.Issue;
import com.library.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findAllByOrderByIssueDateDescIdDesc();

    List<Issue> findByStatusOrderByDueDateAsc(String status);

    List<Issue> findByStatusAndDueDateBeforeOrderByDueDateAsc(String status, LocalDate date);

    List<Issue> findByUserOrderByIssueDateDescIdDesc(User user);

    List<Issue> findByUserAndStatusOrderByDueDateAsc(User user, String status);

    List<Issue> findTop5ByOrderByIdDesc();

    long countByStatus(String status);

    long countByStatusAndDueDateBefore(String status, LocalDate date);

    long countByUserAndStatus(User user, String status);

    boolean existsByBookAndUserAndStatus(Book book, User user, String status);

    boolean existsByBook(Book book);
}
