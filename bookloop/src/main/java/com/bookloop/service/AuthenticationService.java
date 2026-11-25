package com.bookloop.service;

import com.bookloop.dao.UserDAO;
import com.bookloop.exception.AuthenticationException;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.Student;
import com.bookloop.model.User;
import com.bookloop.util.PasswordUtil;
import com.bookloop.util.ValidationUtil;

public class AuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String email, String password) throws AuthenticationException, DatabaseException, ValidationException {
        if (!ValidationUtil.isNotBlank(email)) {
            throw new ValidationException("Email address is required.");
        }
        if (!ValidationUtil.isNotBlank(password)) {
            throw new ValidationException("Password is required.");
        }

        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new AuthenticationException("No account found with this email address.");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("This account has been deactivated. Please contact administrator.");
        }

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid password. Please check and try again.");
        }

        return user;
    }

    public Student register(String name, String studentId, String email,
                            String password, String confirmPassword,
                            String department, int year, int semester,
                            String phone) throws ValidationException, DatabaseException {

        validateRegistration(name, studentId, email, password, confirmPassword, department, year, semester, phone);

        if (userDAO.emailExists(email.trim().toLowerCase())) {
            throw new ValidationException("An account with this email address already exists.");
        }

        if (userDAO.studentIdExists(studentId.trim().toUpperCase())) {
            throw new ValidationException("A student with this Student ID is already registered.");
        }

        String passwordHash = PasswordUtil.hash(password);

        Student student = new Student();
        student.setName(name.trim());
        student.setStudentId(studentId.trim().toUpperCase());
        student.setEmail(email.trim().toLowerCase());
        student.setDepartment(department.trim());
        student.setYearOfStudy(year);
        student.setSemester(semester);
        student.setPhone(phone != null ? phone.trim() : null);

        int generatedId = userDAO.createStudent(student, passwordHash);
        student.setId(generatedId);

        return student;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword, String confirmNew)
            throws ValidationException, DatabaseException, AuthenticationException {

        if (!ValidationUtil.isNotBlank(oldPassword)) {
            throw new ValidationException("Current password is required.");
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }
        if (!newPassword.equals(confirmNew)) {
            throw new ValidationException("New password and confirmation do not match.");
        }

        Student student = userDAO.findStudentById(userId);
        if (student == null) {
            throw new AuthenticationException("User account not found.");
        }

        if (!PasswordUtil.verify(oldPassword, student.getPasswordHash())) {
            throw new AuthenticationException("Current password entered is incorrect.");
        }

        String newHash = PasswordUtil.hash(newPassword);
        return userDAO.updatePassword(userId, newHash);
    }

    public boolean updateProfile(Student student) throws ValidationException, DatabaseException {
        if (!ValidationUtil.isNotBlank(student.getName())) {
            throw new ValidationException("Name cannot be empty.");
        }
        if (!ValidationUtil.isValidPhone(student.getPhone())) {
            throw new ValidationException("Phone number is not valid (10-15 digits required).");
        }
        return userDAO.updateProfile(student);
    }

    private void validateRegistration(String name, String studentId, String email,
                                      String password, String confirmPassword,
                                      String department, int year, int semester,
                                      String phone) throws ValidationException {

        if (!ValidationUtil.isNotBlank(name) || name.trim().length() < 2) {
            throw new ValidationException("Please enter your full legal or college name.");
        }
        if (!ValidationUtil.isValidStudentId(studentId)) {
            throw new ValidationException("Please enter a valid Student ID.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Password and Confirm Password do not match.");
        }
        if (!ValidationUtil.isNotBlank(department)) {
            throw new ValidationException("Please select your academic department.");
        }
        if (year < 1 || year > 5) {
            throw new ValidationException("Year of study must be between 1 and 5.");
        }
        if (semester < 1 || semester > 10) {
            throw new ValidationException("Semester must be between 1 and 10.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("Phone number format is invalid.");
        }
    }
}
