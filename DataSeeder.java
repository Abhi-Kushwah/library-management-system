package com.library.lms.config;

import com.library.lms.model.Book;
import com.library.lms.model.User;
import com.library.lms.repository.BookRepository;
import com.library.lms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// puts a default admin and some books in an empty database so the app is usable on first run
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, BookRepository bookRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@lms.com")) {
            addUser("Librarian", "admin@lms.com", "9999999999", "admin123", "ADMIN");
        }
        if (!userRepository.existsByEmail("rahul@lms.com")) {
            addUser("Rahul Sharma", "rahul@lms.com", "9876543210", "rahul123", "MEMBER");
        }
        if (!userRepository.existsByEmail("priya@lms.com")) {
            addUser("Priya Verma", "priya@lms.com", "9123456780", "priya123", "MEMBER");
        }

        if (bookRepository.count() == 0) {
            addBook("9780134685991", "Effective Java", "Joshua Bloch", "Programming", 3);
            addBook("9780596009205", "Head First Java", "Kathy Sierra", "Programming", 4);
            addBook("9780132350884", "Clean Code", "Robert C. Martin", "Programming", 2);
            addBook("9781617297571", "Spring in Action", "Craig Walls", "Programming", 2);
            addBook("9780078022159", "Database System Concepts", "Abraham Silberschatz", "Database", 3);
            addBook("9780262046305", "Introduction to Algorithms", "Thomas H. Cormen", "Algorithms", 2);
            addBook("9781119800361", "Operating System Concepts", "Abraham Silberschatz", "Operating Systems", 3);
            addBook("9780135957059", "The Pragmatic Programmer", "David Thomas", "Programming", 1);
        }
    }

    private void addUser(String name, String email, String phone, String rawPassword, String role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPhone(phone);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        userRepository.save(u);
    }

    private void addBook(String isbn, String title, String author, String category, int copies) {
        Book b = new Book();
        b.setIsbn(isbn);
        b.setTitle(title);
        b.setAuthor(author);
        b.setCategory(category);
        b.setTotalCopies(copies);
        b.setAvailableCopies(copies);
        bookRepository.save(b);
    }
}
