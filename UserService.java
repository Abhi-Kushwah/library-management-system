package com.library.lms.service;

import com.library.lms.model.User;
import com.library.lms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllMembers() {
        return userRepository.findByRoleOrderByNameAsc("MEMBER");
    }

    public void addMember(String name, String email, String phone, String password) {
        if (name == null || name.isBlank() || email == null || email.isBlank()
                || password == null || password.length() < 6) {
            throw new IllegalArgumentException("Name, email and a password of at least 6 characters are required");
        }

        String cleanEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("This email is already registered");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(cleanEmail);
        user.setPhone(phone == null ? null : phone.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("MEMBER");
        userRepository.save(user);
    }
}
