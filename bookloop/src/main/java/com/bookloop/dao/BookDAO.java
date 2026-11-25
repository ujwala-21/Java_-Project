package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Book;
import com.bookloop.model.Course;
import com.bookloop.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public int createBook(Book book) throws DatabaseException {
        String sql = "INSERT INTO books (title, author, isbn, edition, subject, department_id, description, cover_image, course_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getEdition());
            stmt.setString(5, book.getSubject());
            if (book.getDepartmentId() > 0) stmt.setInt(6, book.getDepartmentId());
            else stmt.setNull(6, Types.INTEGER);
            stmt.setString(7, book.getDescription());
            stmt.setString(8, book.getCoverImage());
            if (book.getCourseId() != null) stmt.setInt(9, book.getCourseId());
            else stmt.setNull(9, Types.INTEGER);

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    book.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert book: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public Book findById(int id) throws DatabaseException {
        String sql = "SELECT b.*, d.name as department_name FROM books b " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "WHERE b.id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find book by ID: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public List<Book> findAll() throws DatabaseException {
        String sql = "SELECT b.*, d.name as department_name FROM books b " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "ORDER BY b.title ASC";
        List<Book> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load all books: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public List<Book> search(String query) throws DatabaseException {
        String sql = "SELECT b.*, d.name as department_name FROM books b " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "WHERE b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ? OR b.subject LIKE ? " +
                     "ORDER BY b.title ASC";
        List<Book> list = new ArrayList<>();
        String like = "%" + query.trim() + "%";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            stmt.setString(4, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search books: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public List<Department> getAllDepartments() throws DatabaseException {
        String sql = "SELECT * FROM departments ORDER BY name ASC";
        List<Department> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Department d = new Department();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setCode(rs.getString("code"));
                d.setDescription(rs.getString("description"));
                list.add(d);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch departments: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public List<Course> getCoursesByDepartment(int departmentId) throws DatabaseException {
        String sql = "SELECT c.*, d.name as department_name FROM courses c " +
                     "JOIN departments d ON c.department_id = d.id " +
                     "WHERE c.department_id = ? ORDER BY c.year_of_study, c.semester";
        List<Course> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setDepartmentId(rs.getInt("department_id"));
                    c.setDepartmentName(rs.getString("department_name"));
                    c.setName(rs.getString("name"));
                    c.setCode(rs.getString("code"));
                    c.setYearOfStudy(rs.getInt("year_of_study"));
                    c.setSemester(rs.getInt("semester"));
                    c.setSubject(rs.getString("subject"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch courses: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public List<Book> getRecommendedBooks(int departmentId, int year, int semester) throws DatabaseException {
        String sql = "SELECT b.*, d.name as department_name FROM books b " +
                     "JOIN courses c ON b.course_id = c.id " +
                     "JOIN departments d ON b.department_id = d.id " +
                     "WHERE c.department_id = ? AND c.year_of_study = ? AND c.semester = ? " +
                     "ORDER BY b.title ASC";
        List<Book> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            stmt.setInt(2, year);
            stmt.setInt(3, semester);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get recommended books: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    private Book mapBookFromResultSet(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setIsbn(rs.getString("isbn"));
        b.setEdition(rs.getString("edition"));
        b.setSubject(rs.getString("subject"));
        b.setDepartmentId(rs.getInt("department_id"));
        b.setDepartmentName(rs.getString("department_name"));
        b.setDescription(rs.getString("description"));
        b.setCoverImage(rs.getString("cover_image"));
        int courseId = rs.getInt("course_id");
        if (!rs.wasNull()) b.setCourseId(courseId);
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) b.setCreatedAt(ts.toLocalDateTime());
        return b;
    }
}
