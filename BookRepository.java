package com.library.lms.repository;

import com.library.lms.model.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findByAvailableCopiesGreaterThanOrderByTitleAsc(int copies);

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    @Query("select b from Book b where lower(b.title) like lower(concat('%', :q, '%')) "
            + "or lower(b.author) like lower(concat('%', :q, '%')) "
            + "or lower(b.isbn) like lower(concat('%', :q, '%')) "
            + "or lower(b.category) like lower(concat('%', :q, '%')) "
            + "order by b.title")
    List<Book> search(@Param("q") String q);

    @Query("select coalesce(sum(b.availableCopies), 0) from Book b")
    long totalAvailableCopies();

    // row lock so two people can't take the last copy at the same time
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findForUpdate(@Param("id") Long id);
}
