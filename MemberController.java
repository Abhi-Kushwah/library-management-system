package com.library.lms.controller;

import com.library.lms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {

    private final UserService userService;

    public MemberController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members", userService.findAllMembers());
        return "members";
    }

    @GetMapping("/members/new")
    public String newForm() {
        return "member-form";
    }

    @PostMapping("/members/save")
    public String save(@RequestParam String name, @RequestParam String email,
                       @RequestParam(required = false) String phone, @RequestParam String password,
                       Model model, RedirectAttributes ra) {
        try {
            userService.addMember(name, email, phone, password);
            ra.addFlashAttribute("msg", "Member added successfully");
            return "redirect:/members";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            return "member-form";
        }
    }
}
