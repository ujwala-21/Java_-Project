package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Book;
import com.bookloop.model.BookListing;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ListingDAO {

    public int createListing(BookListing listing) throws DatabaseException {
        String sql = "INSERT INTO book_listings (book_id, owner_id, listing_type, condition_type, original_price, " +
                     "asking_price, credit_value, description, condition_details, has_highlights, has_annotations, " +
                     "has_missing_pages, has_damaged_cover, has_torn_pages, has_water_damage, rental_duration_days, " +
                     "rental_credits, image_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, listing.getBookId());
            stmt.setInt(2, listing.getOwnerId());
            stmt.setString(3, listing.getListingType().name());
            stmt.setString(4, listing.getConditionType().name());
            stmt.setBigDecimal(5, listing.getOriginalPrice());
            stmt.setBigDecimal(6, listing.getAskingPrice());
            stmt.setBigDecimal(7, listing.getCreditValue());
            stmt.setString(8, listing.getDescription());
            stmt.setString(9, listing.getConditionDetails());
            stmt.setBoolean(10, listing.isHasHighlights());
            stmt.setBoolean(11, listing.isHasAnnotations());
            stmt.setBoolean(12, listing.isHasMissingPages());
            stmt.setBoolean(13, listing.isHasDamagedCover());
            stmt.setBoolean(14, listing.isHasTornPages());
            stmt.setBoolean(15, listing.isHasWaterDamage());
            if (listing.getRentalDurationDays() != null) stmt.setInt(16, listing.getRentalDurationDays());
            else stmt.setNull(16, Types.INTEGER);
            stmt.setBigDecimal(17, listing.getRentalCredits());
            stmt.setString(18, listing.getImagePath());
            stmt.setString(19, listing.getStatus() != null ? listing.getStatus().name() : "AVAILABLE");

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    listing.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create book listing: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public BookListing findById(int id) throws DatabaseException {
        String sql = "SELECT l.*, b.title as book_title, b.author as book_author, b.isbn, b.edition, " +
                     "b.subject, b.department_id, d.name as department_name, u.name as owner_name, u.email as owner_email " +
                     "FROM book_listings l " +
                     "JOIN books b ON l.book_id = b.id " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "JOIN users u ON l.owner_id = u.id " +
                     "WHERE l.id = ?";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapListingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find listing by ID: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public List<BookListing> searchListings(String query, String type, String condition,
                                           Integer deptId, Double maxPrice, String sortBy) throws DatabaseException {
        StringBuilder sql = new StringBuilder(
            "SELECT l.*, b.title as book_title, b.author as book_author, b.isbn, b.edition, " +
            "b.subject, b.department_id, d.name as department_name, u.name as owner_name, u.email as owner_email " +
            "FROM book_listings l " +
            "JOIN books b ON l.book_id = b.id " +
            "LEFT JOIN departments d ON b.department_id = d.id " +
            "JOIN users u ON l.owner_id = u.id " +
            "WHERE l.status = 'AVAILABLE' "
        );

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ? OR b.subject LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }

        if (type != null && !type.equalsIgnoreCase("ALL")) {
            sql.append("AND l.listing_type = ? ");
            params.add(type.toUpperCase());
        }

        if (condition != null && !condition.equalsIgnoreCase("ALL")) {
            sql.append("AND l.condition_type = ? ");
            params.add(condition.toUpperCase());
        }

        if (deptId != null && deptId > 0) {
            sql.append("AND b.department_id = ? ");
            params.add(deptId);
        }

        if (maxPrice != null && maxPrice > 0) {
            sql.append("AND (l.asking_price <= ? OR l.credit_value <= ?) ");
            params.add(maxPrice);
            params.add(maxPrice);
        }

        if ("PRICE_LOW".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY COALESCE(l.asking_price, l.credit_value, 0) ASC ");
        } else if ("PRICE_HIGH".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY COALESCE(l.asking_price, l.credit_value, 0) DESC ");
        } else {
            sql.append("ORDER BY l.created_at DESC ");
        }

        List<BookListing> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapListingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search listings: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public List<BookListing> findByOwner(int ownerId) throws DatabaseException {
        String sql = "SELECT l.*, b.title as book_title, b.author as book_author, b.isbn, b.edition, " +
                     "b.subject, b.department_id, d.name as department_name, u.name as owner_name, u.email as owner_email " +
                     "FROM book_listings l " +
                     "JOIN books b ON l.book_id = b.id " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "JOIN users u ON l.owner_id = u.id " +
                     "WHERE l.owner_id = ? ORDER BY l.created_at DESC";

        List<BookListing> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapListingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find listings by owner: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public List<BookListing> findAvailableForExchange(int excludeOwnerId) throws DatabaseException {
        String sql = "SELECT l.*, b.title as book_title, b.author as book_author, b.isbn, b.edition, " +
                     "b.subject, b.department_id, d.name as department_name, u.name as owner_name, u.email as owner_email " +
                     "FROM book_listings l " +
                     "JOIN books b ON l.book_id = b.id " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "JOIN users u ON l.owner_id = u.id " +
                     "WHERE l.status = 'AVAILABLE' AND l.listing_type = 'EXCHANGE' AND l.owner_id != ? " +
                     "ORDER BY l.created_at DESC";

        List<BookListing> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, excludeOwnerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapListingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load exchange listings: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public boolean updateStatus(int listingId, String status) throws DatabaseException {
        String sql = "UPDATE book_listings SET status = ? WHERE id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, listingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update listing status: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public boolean updateListing(BookListing listing) throws DatabaseException {
        String sql = "UPDATE book_listings SET listing_type = ?, condition_type = ?, original_price = ?, " +
                     "asking_price = ?, credit_value = ?, description = ?, condition_details = ?, " +
                     "has_highlights = ?, has_annotations = ?, has_missing_pages = ?, has_damaged_cover = ?, " +
                     "has_torn_pages = ?, has_water_damage = ?, rental_duration_days = ?, rental_credits = ?, " +
                     "image_path = ? WHERE id = ? AND owner_id = ?";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, listing.getListingType().name());
            stmt.setString(2, listing.getConditionType().name());
            stmt.setBigDecimal(3, listing.getOriginalPrice());
            stmt.setBigDecimal(4, listing.getAskingPrice());
            stmt.setBigDecimal(5, listing.getCreditValue());
            stmt.setString(6, listing.getDescription());
            stmt.setString(7, listing.getConditionDetails());
            stmt.setBoolean(8, listing.isHasHighlights());
            stmt.setBoolean(9, listing.isHasAnnotations());
            stmt.setBoolean(10, listing.isHasMissingPages());
            stmt.setBoolean(11, listing.isHasDamagedCover());
            stmt.setBoolean(12, listing.isHasTornPages());
            stmt.setBoolean(13, listing.isHasWaterDamage());
            if (listing.getRentalDurationDays() != null) stmt.setInt(14, listing.getRentalDurationDays());
            else stmt.setNull(14, Types.INTEGER);
            stmt.setBigDecimal(15, listing.getRentalCredits());
            stmt.setString(16, listing.getImagePath());
            stmt.setInt(17, listing.getId());
            stmt.setInt(18, listing.getOwnerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update listing: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public boolean deleteListing(int listingId, int ownerId) throws DatabaseException {
        String sql = "DELETE FROM book_listings WHERE id = ? AND owner_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            stmt.setInt(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete listing: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public int getActiveListingCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM book_listings WHERE status = 'AVAILABLE'";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count active listings: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 0;
    }

    public int getAvailableListingCountForBook(int bookId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM book_listings WHERE book_id = ? AND status = 'AVAILABLE'";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count listings for book: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 0;
    }

    public List<BookListing> findAll() throws DatabaseException {
        String sql = "SELECT l.*, b.title as book_title, b.author as book_author, b.isbn, b.edition, " +
                     "b.subject, b.department_id, d.name as department_name, u.name as owner_name, u.email as owner_email " +
                     "FROM book_listings l " +
                     "JOIN books b ON l.book_id = b.id " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "JOIN users u ON l.owner_id = u.id " +
                     "ORDER BY l.created_at DESC";

        List<BookListing> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapListingFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all listings: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public BookListing findFirstByBookId(int bookId) throws DatabaseException {
        String sql = "SELECT l.*, b.title as book_title, b.author as book_author, b.isbn, b.edition, " +
                     "b.subject, b.department_id, d.name as department_name, u.name as owner_name, u.email as owner_email " +
                     "FROM book_listings l " +
                     "JOIN books b ON l.book_id = b.id " +
                     "LEFT JOIN departments d ON b.department_id = d.id " +
                     "JOIN users u ON l.owner_id = u.id " +
                     "WHERE l.book_id = ? ORDER BY FIELD(l.status, 'AVAILABLE', 'RESERVED', 'COMPLETED', 'SOLD') ASC, l.created_at DESC LIMIT 1";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapListingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find listing by book ID: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    private BookListing mapListingFromResultSet(ResultSet rs) throws SQLException {
        BookListing l = new BookListing();
        l.setId(rs.getInt("id"));
        l.setBookId(rs.getInt("book_id"));
        l.setOwnerId(rs.getInt("owner_id"));
        l.setOwnerName(rs.getString("owner_name"));

        Book b = new Book();
        b.setId(rs.getInt("book_id"));
        b.setTitle(rs.getString("book_title"));
        b.setAuthor(rs.getString("book_author"));
        b.setIsbn(rs.getString("isbn"));
        b.setEdition(rs.getString("edition"));
        b.setSubject(rs.getString("subject"));
        b.setDepartmentName(rs.getString("department_name"));
        l.setBook(b);

        l.setListingType(BookListing.ListingType.valueOf(rs.getString("listing_type")));
        l.setConditionType(BookListing.ConditionType.valueOf(rs.getString("condition_type")));
        l.setOriginalPrice(rs.getBigDecimal("original_price"));
        l.setAskingPrice(rs.getBigDecimal("asking_price"));
        l.setCreditValue(rs.getBigDecimal("credit_value"));
        l.setDescription(rs.getString("description"));
        l.setConditionDetails(rs.getString("condition_details"));
        l.setHasHighlights(rs.getBoolean("has_highlights"));
        l.setHasAnnotations(rs.getBoolean("has_annotations"));
        l.setHasMissingPages(rs.getBoolean("has_missing_pages"));
        l.setHasDamagedCover(rs.getBoolean("has_damaged_cover"));
        l.setHasTornPages(rs.getBoolean("has_torn_pages"));
        l.setHasWaterDamage(rs.getBoolean("has_water_damage"));

        int rDays = rs.getInt("rental_duration_days");
        if (!rs.wasNull()) l.setRentalDurationDays(rDays);
        l.setRentalCredits(rs.getBigDecimal("rental_credits"));
        l.setImagePath(rs.getString("image_path"));
        l.setStatus(BookListing.ListingStatus.valueOf(rs.getString("status")));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) l.setCreatedAt(ts.toLocalDateTime());
        return l;
    }
}
