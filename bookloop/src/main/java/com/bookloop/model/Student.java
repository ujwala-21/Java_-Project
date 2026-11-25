package com.bookloop.model;

public class Student extends User {
    private String studentId;
    private String department;
    private int yearOfStudy;
    private int semester;
    private CreditWallet wallet;

    public Student() { setRole(UserRole.STUDENT); }

    public Student(int id, String name, String email, String studentId, String department, int year, int semester) {
        super(id, name, email, UserRole.STUDENT);
        this.studentId = studentId;
        this.department = department;
        this.yearOfStudy = year;
        this.semester = semester;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public CreditWallet getWallet() { return wallet; }
    public void setWallet(CreditWallet wallet) { this.wallet = wallet; }

    @Override
    public String toString() {
        return "Student{id=" + getId() + ", name='" + getName() + "', studentId='" + studentId + "', dept='" + department + "'}";
    }
}
