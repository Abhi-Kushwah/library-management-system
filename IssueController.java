package com.library.lms.controller;

import com.library.lms.model.Issue;
import com.library.lms.model.User;
import com.library.lms.repository.BookRepository;
import com.library.lms.service.IssueService;
import com.library.lms.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;
    private final BookRepository bookRepository;

    public IssueController(IssueService issueService, UserService userService,
                           BookRepository bookRepository) {
        this.issueService = issueService;
        this.userService = userService;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/issues")
    public String list(@RequestParam(defaultValue = "all") String filter, Model model) {
        model.addAttribute("issues", issueService.findByFilter(filter));
        model.addAttribute("filter", filter);
        return "issues";
    }

    @GetMapping("/issues/new")
    public String issueForm(Model model) {
        model.addAttribute("books", bookRepository.findByAvailableCopiesGreaterThanOrderByTitleAsc(0));
        model.addAttribute("members", userService.findAllMembers());
        model.addAttribute("loanDays", IssueService.LOAN_DAYS);
        return "issue-form";
    }

    @PostMapping("/issues/issue")
    public String issue(@RequestParam Long bookId, @RequestParam Long userId, RedirectAttributes ra) {
        try {
            Issue issue = issueService.issueBook(bookId, userId);
            ra.addFlashAttribute("msg", "'" + issue.getBook().getTitle() + "' issued to "
                    + issue.getUser().getName() + ". Due on " + issue.getDueDate());
            return "redirect:/issues?filter=active";
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/issues/new";
        }
    }

    @PostMapping("/issues/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Issue issue = issueService.returnBook(id);
            String msg = "'" + issue.getBook().getTitle() + "' returned by " + issue.getUser().getName() + ".";
            if (issue.getFine() > 0) {
                msg += " Late fine: Rs. " + issue.getFine();
            }
            ra.addFlashAttribute("msg", msg);
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/issues?filter=active";
    }

    @GetMapping("/my-books")
    public String myBooks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        model.addAttribute("issues", issueService.findByUser(user));
        return "my-books";
    }
}
