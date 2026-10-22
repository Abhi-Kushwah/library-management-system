package com.library.lms.config;

import com.library.lms.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        User user = (User) request.getSession().getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (isAdminOnly(request.getRequestURI()) && !"ADMIN".equals(user.getRole())) {
            response.sendRedirect("/dashboard");
            return false;
        }
        return true;
    }

    private boolean isAdminOnly(String path) {
        return path.startsWith("/members")
                || path.startsWith("/issues")
                || path.equals("/books/new")
                || path.equals("/books/save")
                || path.matches("/books/\\d+/(edit|delete)");
    }
}
