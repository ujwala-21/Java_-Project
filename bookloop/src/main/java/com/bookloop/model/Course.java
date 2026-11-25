package com.bookloop.model;

import java.time.LocalDateTime;

public class Course {
    private int id;
    private int departmentId;
    private String departmentName;
    private String name;
    private String code;
    private int yearOfStudy;
    private int semester;
    private String subject;
    private LocalDateTime createdAt;

    public Course() {}

    public Course(int id, int departmentId, String name, String code, int yearOfStudy, int semester, String subject) {
        this.id = id;
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
        this.yearOfStudy = yearOfStudy;
        this.semester = semester;
        this.subject = subject;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return code != null ? code + " - " + name : name;
    }
}
