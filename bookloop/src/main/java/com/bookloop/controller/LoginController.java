package com.bookloop.controller;

import com.bookloop.exception.AuthenticationException;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.Student;
import com.bookloop.model.User;
import com.bookloop.service.AuthenticationService;
import com.bookloop.util.SessionManager;

public class LoginController {
    private final AuthenticationService authService;

    public LoginController() {
        this.authService = new AuthenticationService();
    }

    public LoginController(AuthenticationService authService) {
        this.authService = authService;
    }

    public User handleLogin(String email, String password) throws AuthenticationException, DatabaseException, ValidationException {
        User user = authService.login(email, password);
        SessionManager.getInstance().login(user);
        return user;
    }

    public Student handleRegister(String name, String studentId, String email,
                                 String password, String confirmPassword,
                                 String department, int year, int semester,
                                 String phone) throws ValidationException, DatabaseException {
        Student student = authService.register(name, studentId, email, password, confirmPassword, department, year, semester, phone);
        SessionManager.getInstance().login(student);
        return student;
    }

    public void handleLogout() {
        SessionManager.getInstance().logout();
    }
}
