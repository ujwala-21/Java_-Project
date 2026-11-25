package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Admin;
import com.bookloop.model.CreditWallet;
import com.bookloop.model.Student;
import com.bookloop.model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) throws DatabaseException {
        String sql = "SELECT u.*, s.student_id, s.department, s.year_of_study, s.semester, " +
                     "cw.id as wallet_id, cw.balance as wallet_balance " +
                     "FROM users u " +
                     "LEFT JOIN students s ON u.id = s.id " +
                     "LEFT JOIN credit_wallets cw ON u.id = cw.student_id " +
                     "WHERE u.email = ? AND u.is_active = TRUE";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by email: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public Student findStudentById(int id) throws DatabaseException {
        String sql = "SELECT u.*, s.student_id, s.department, s.year_of_study, s.semester, " +
                     "cw.id as wallet_id, cw.balance as wallet_balance " +
                     "FROM users u " +
                     "JOIN students s ON u.id = s.id " +
                     "LEFT JOIN credit_wallets cw ON u.id = cw.student_id " +
                     "WHERE u.id = ?";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapUserFromResultSet(rs);
                    if (user instanceof Student) {
                        return (Student) user;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student by ID: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public boolean emailExists(String email) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check email existence: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }

    public boolean studentIdExists(String studentId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check student ID existence: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }

    public int createStudent(Student student, String passwordHash) throws DatabaseException {
        String insertUserSql = "INSERT INTO users (name, email, password_hash, role, phone, is_active) VALUES (?, ?, ?, 'STUDENT', ?, TRUE)";
        String insertStudentSql = "INSERT INTO students (id, student_id, department, year_of_study, semester) VALUES (?, ?, ?, ?, ?)";
        String insertWalletSql = "INSERT INTO credit_wallets (student_id, balance) VALUES (?, 50.00)"; // 50 welcome credits

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);

            int userId;
            try (PreparedStatement stmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, student.getName());
                stmt.setString(2, student.getEmail());
                stmt.setString(3, passwordHash);
                stmt.setString(4, student.getPhone());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                        student.setId(userId);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertStudentSql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, student.getStudentId());
                stmt.setString(3, student.getDepartment());
                stmt.setInt(4, student.getYearOfStudy());
                stmt.setInt(5, student.getSemester());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertWalletSql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            conn.commit();
            return userId;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                // Ignore rollback failure
            }
            throw new DatabaseException("Failed to register student: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
            db.releaseConnection(conn);
        }
    }

    public boolean updateProfile(Student student) throws DatabaseException {
        String updateUserSql = "UPDATE users SET name = ?, phone = ? WHERE id = ?";
        String updateStudentSql = "UPDATE students SET department = ?, year_of_study = ?, semester = ? WHERE id = ?";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(updateUserSql)) {
                stmt.setString(1, student.getName());
                stmt.setString(2, student.getPhone());
                stmt.setInt(3, student.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateStudentSql)) {
                stmt.setString(1, student.getDepartment());
                stmt.setInt(2, student.getYearOfStudy());
                stmt.setInt(3, student.getSemester());
                stmt.setInt(4, student.getId());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {}
            throw new DatabaseException("Failed to update profile: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
            db.releaseConnection(conn);
        }
    }

    public boolean updatePassword(int userId, String newHash) throws DatabaseException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update password: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public List<Student> getAllStudents() throws DatabaseException {
        String sql = "SELECT u.*, s.student_id, s.department, s.year_of_study, s.semester, " +
                     "cw.id as wallet_id, cw.balance as wallet_balance " +
                     "FROM users u " +
                     "JOIN students s ON u.id = s.id " +
                     "LEFT JOIN credit_wallets cw ON u.id = cw.student_id " +
                     "ORDER BY u.id DESC";

        List<Student> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User u = mapUserFromResultSet(rs);
                if (u instanceof Student) {
                    list.add((Student) u);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public boolean setUserActiveStatus(int userId, boolean isActive) throws DatabaseException {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user active status: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public int getTotalStudentCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM students";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count students: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 0;
    }

    public boolean updateProfilePicture(int userId, String imagePath) throws DatabaseException {
        // Ensure the column exists first
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "ALTER TABLE users ADD COLUMN profile_picture VARCHAR(500) DEFAULT NULL")) {
                stmt.executeUpdate();
            } catch (SQLException ignored) {
                // Column already exists - this is fine
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET profile_picture = ? WHERE id = ?")) {
                if (imagePath != null) {
                    stmt.setString(1, imagePath);
                } else {
                    stmt.setNull(1, java.sql.Types.VARCHAR);
                }
                stmt.setInt(2, userId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update profile picture: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String roleStr = rs.getString("role");
        String hash = rs.getString("password_hash");
        String phone = rs.getString("phone");
        boolean active = rs.getBoolean("is_active");

        if ("ADMIN".equalsIgnoreCase(roleStr)) {
            Admin admin = new Admin(id, name, email);
            admin.setPasswordHash(hash);
            admin.setPhone(phone);
            admin.setActive(active);
            try { admin.setProfilePicture(rs.getString("profile_picture")); } catch (SQLException ignored) {}
            return admin;
        } else {
            String sid = rs.getString("student_id");
            String dept = rs.getString("department");
            int year = rs.getInt("year_of_study");
            int sem = rs.getInt("semester");

            Student student = new Student(id, name, email, sid, dept, year, sem);
            student.setPasswordHash(hash);
            student.setPhone(phone);
            student.setActive(active);
            try { student.setProfilePicture(rs.getString("profile_picture")); } catch (SQLException ignored) {}

            int walletId = rs.getInt("wallet_id");
            if (!rs.wasNull()) {
                CreditWallet wallet = new CreditWallet();
                wallet.setId(walletId);
                wallet.setStudentId(id);
                wallet.setBalance(rs.getBigDecimal("wallet_balance"));
                student.setWallet(wallet);
            }
            return student;
        }
    }
}
