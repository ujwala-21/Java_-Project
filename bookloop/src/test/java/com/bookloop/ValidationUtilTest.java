package com.bookloop;

import com.bookloop.util.PasswordUtil;
import com.bookloop.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    @DisplayName("Email format validation tests")
    public void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("student@university.edu"));
        assertTrue(ValidationUtil.isValidEmail("john.doe12@college.ac.in"));
        assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    @DisplayName("Password validation tests")
    public void testPasswordValidation() {
        assertTrue(ValidationUtil.isValidPassword("StrongPass123"));
        assertTrue(ValidationUtil.isValidPassword("secret1"));
        assertFalse(ValidationUtil.isValidPassword("123")); // too short
        assertFalse(ValidationUtil.isValidPassword(null));
    }

    @Test
    @DisplayName("BCrypt password hashing and verification tests")
    public void testPasswordHashing() {
        String plain = "Student@123";
        String hash = PasswordUtil.hash(plain);

        assertNotNull(hash);
        assertNotEquals(plain, hash);
        assertTrue(PasswordUtil.verify(plain, hash));
        assertFalse(PasswordUtil.verify("WrongPassword", hash));
    }

    @Test
    @DisplayName("Student ID format validation")
    public void testStudentIdValidation() {
        assertTrue(ValidationUtil.isValidStudentId("CS2024001"));
        assertFalse(ValidationUtil.isValidStudentId(""));
        assertFalse(ValidationUtil.isValidStudentId(null));
    }
}
