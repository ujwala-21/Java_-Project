package com.bookloop.service;

import com.bookloop.dao.BookDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.Book;
import com.bookloop.model.Course;
import com.bookloop.model.Department;
import com.bookloop.util.ValidationUtil;

import java.util.List;

public class BookService {
    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public int createOrFindBook(Book book) throws ValidationException, DatabaseException {
        if (!ValidationUtil.isNotBlank(book.getTitle())) {
            throw new ValidationException("Book title is required.");
        }
        if (!ValidationUtil.isNotBlank(book.getAuthor())) {
            throw new ValidationException("Book author is required.");
        }
        if (!ValidationUtil.isValidIsbn(book.getIsbn())) {
            throw new ValidationException("Invalid ISBN format. Must be 10 or 13 digits.");
        }

        // Check if book with same title & author already exists
        List<Book> existing = bookDAO.search(book.getTitle().trim());
        for (Book b : existing) {
            if (b.getTitle().equalsIgnoreCase(book.getTitle().trim()) &&
                b.getAuthor().equalsIgnoreCase(book.getAuthor().trim())) {
                return b.getId();
            }
        }

        return bookDAO.createBook(book);
    }

    public Book getBookById(int id) throws DatabaseException {
        return bookDAO.findById(id);
    }

    public List<Book> getAllBooks() throws DatabaseException {
        return bookDAO.findAll();
    }

    public List<Book> searchBooks(String query) throws DatabaseException {
        if (!ValidationUtil.isNotBlank(query)) {
            return bookDAO.findAll();
        }
        return bookDAO.search(query);
    }

    public List<Department> getAllDepartments() throws DatabaseException {
        return bookDAO.getAllDepartments();
    }

    public List<Course> getCoursesByDepartment(int departmentId) throws DatabaseException {
        return bookDAO.getCoursesByDepartment(departmentId);
    }

    public List<Book> getRecommendedBooks(int departmentId, int year, int semester) throws DatabaseException {
        return bookDAO.getRecommendedBooks(departmentId, year, semester);
    }
}
