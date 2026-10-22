package com.library.lms.service;

import com.library.lms.model.Book;
import com.library.lms.repository.BookRepository;
import com.library.lms.repository.IssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final IssueRepository issueRepository;

    public BookService(BookRepository bookRepository, IssueRepository issueRepository) {
        this.bookRepository = bookRepository;
        this.issueRepository = issueRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    public List<Book> search(String keyword) {
        return bookRepository.search(keyword);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    @Transactional
    public void save(Book form) {
        String isbn = form.getIsbn().trim();

        if (form.getId() == null) {
            if (bookRepository.existsByIsbn(isbn)) {
                throw new IllegalArgumentException("A book with this ISBN already exists");
            }
            form.setIsbn(isbn);
            form.setAvailableCopies(form.getTotalCopies());
            bookRepository.save(form);
            return;
        }

        Book book = findById(form.getId());
        bookRepository.findByIsbn(isbn).ifPresent(other -> {
            if (!other.getId().equals(book.getId())) {
                throw new IllegalArgumentException("Another book already uses this ISBN");
            }
        });

        int currentlyIssued = book.getTotalCopies() - book.getAvailableCopies();
        if (form.getTotalCopies() < currentlyIssued) {
            throw new IllegalArgumentException(
                    currentlyIssued + " copies are currently issued, so total copies cannot be less than that");
        }

        book.setIsbn(isbn);
        book.setTitle(form.getTitle().trim());
        book.setAuthor(form.getAuthor().trim());
        book.setCategory(form.getCategory());
        book.setTotalCopies(form.getTotalCopies());
        book.setAvailableCopies(form.getTotalCopies() - currentlyIssued);
        bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        if (issueRepository.existsByBook(book)) {
            throw new IllegalStateException("This book has issue records, so it cannot be deleted");
        }
        bookRepository.delete(book);
    }
}
