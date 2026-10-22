package com.library.lms.controller;

import com.library.lms.model.User;
import com.library.lms.repository.BookRepository;
import com.library.lms.repository.IssueRepository;
import com.library.lms.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;

    public DashboardController(BookRepository bookRepository, UserRepository userRepository,
                               IssueRepository issueRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        LocalDate today = LocalDate.now();

        model.addAttribute("totalTitles", bookRepository.count());
        model.addAttribute("availableCopies", bookRepository.totalAvailableCopies());

        if ("ADMIN".equals(user.getRole())) {
            model.addAttribute("totalMembers", userRepository.countByRole("MEMBER"));
            model.addAttribute("issuedNow", issueRepository.countByStatus("ISSUED"));
            model.addAttribute("overdueCount", issueRepository.countByStatusAndDueDateBefore("ISSUED", today));
            model.addAttribute("recentIssues", issueRepository.findTop5ByOrderByIdDesc());
        } else {
            model.addAttribute("myIssues", issueRepository.findByUserAndStatusOrderByDueDateAsc(user, "ISSUED"));
        }
        return "dashboard";
    }
}
