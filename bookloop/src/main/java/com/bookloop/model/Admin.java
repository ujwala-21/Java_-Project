package com.bookloop.model;

public class Admin extends User {
    public Admin() { setRole(UserRole.ADMIN); }

    public Admin(int id, String name, String email) {
        super(id, name, email, UserRole.ADMIN);
    }

    @Override
    public String toString() {
        return "Admin{id=" + getId() + ", name='" + getName() + "'}";
    }
}
