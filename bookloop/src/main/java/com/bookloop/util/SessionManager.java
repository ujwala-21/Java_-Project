package com.bookloop.util;

import com.bookloop.model.Student;
import com.bookloop.model.User;

public class SessionManager {
    private static volatile SessionManager instance;
    private User currentUser;
    private Student currentStudent;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        if (user instanceof Student) {
            this.currentStudent = (Student) user;
        } else {
            this.currentStudent = null;
        }
    }

    public void logout() {
        this.currentUser = null;
        this.currentStudent = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        this.currentUser = student;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean isStudent() {
        return currentUser != null && currentUser.isStudent();
    }

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
