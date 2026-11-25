package com.bookloop.model;

import java.time.LocalDateTime;

public class WishlistItem {
    private int id;
    private int studentId;
    private Integer bookId;
    private String bookTitle;
    private String author;
    private String subject;
    private Integer departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
    private int availableListingsCount;

    public WishlistItem() {}

    public WishlistItem(int studentId, Integer bookId, String bookTitle, String author) {
        this.studentId = studentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getAvailableListingsCount() { return availableListingsCount; }
    public void setAvailableListingsCount(int availableListingsCount) { this.availableListingsCount = availableListingsCount; }

    @Override
    public String toString() {
        return bookTitle + (author != null ? " by " + author : "");
    }
}
