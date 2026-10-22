package com.library.lms.controller;

import com.library.lms.model.Book;
import com.library.lms.service.BookService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public String list(@RequestParam(required = false) String q, Model model) {
        List<Book> books = (q == null || q.isBlank()) ? bookService.findAll() : bookService.search(q.trim());
        model.addAttribute("books", books);
        model.addAttribute("q", q);
        return "books";
    }

    @GetMapping("/books/new")
    public String newForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @GetMapping("/books/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("book", bookService.findById(id));
            return "book-form";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/books";
        }
    }

    @PostMapping("/books/save")
    public String save(@Valid @ModelAttribute("book") Book book, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "book-form";
        }
        try {
            boolean isNew = book.getId() == null;
            bookService.save(book);
            ra.addFlashAttribute("msg", isNew ? "Book added successfully" : "Book updated successfully");
            return "redirect:/books";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "book-form";
        }
    }

    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            bookService.delete(id);
            ra.addFlashAttribute("msg", "Book deleted");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books";
    }
}
