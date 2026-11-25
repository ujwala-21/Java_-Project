package com.bookloop.algorithm;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.dao.ListingDAO;
import com.bookloop.dao.WishlistDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.BookListing;
import com.bookloop.model.ExchangeCycle;
import com.bookloop.model.MatchResult;
import com.bookloop.model.WishlistItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MatchingEngine {
    private final ListingDAO listingDAO;
    private final WishlistDAO wishlistDAO;

    public MatchingEngine() {
        this.listingDAO = new ListingDAO();
        this.wishlistDAO = new WishlistDAO();
    }

    public ExchangeGraph buildGraph() throws DatabaseException {
        ExchangeGraph graph = new ExchangeGraph();

        // 1. Fetch all AVAILABLE exchange listings
        List<BookListing> exchangeListings = listingDAO.findAvailableForExchange(-1);

        // 2. Fetch all wishlist items across all students: (student_id, book_id, book_title)
        String sql = "SELECT w.student_id, u.name as student_name, w.book_id, w.book_title " +
                     "FROM wishlist_items w " +
                     "JOIN users u ON w.student_id = u.id";

        Map<Integer, List<Integer>> bookWantersMap = new HashMap<>(); // bookId -> list of studentIds
        Map<String, List<Integer>> titleWantersMap = new HashMap<>(); // lowerTitle -> list of studentIds

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int sId = rs.getInt("student_id");
                String sName = rs.getString("student_name");
                int bId = rs.getInt("book_id");
                String title = rs.getString("book_title");

                graph.addStudent(sId, sName);

                if (bId > 0) {
                    bookWantersMap.computeIfAbsent(bId, k -> new ArrayList<>()).add(sId);
                }
                if (title != null && !title.isEmpty()) {
                    titleWantersMap.computeIfAbsent(title.toLowerCase().trim(), k -> new ArrayList<>()).add(sId);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load wishlist graph connections: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }

        // 3. Connect directed edges: Owner -> Wanter
        for (BookListing l : exchangeListings) {
            int ownerId = l.getOwnerId();
            graph.addStudent(ownerId, l.getOwnerName());

            String bookTitle = l.getBook() != null ? l.getBook().getTitle() : "Textbook";
            Set<Integer> wanters = new HashSet<>();

            if (l.getBookId() > 0 && bookWantersMap.containsKey(l.getBookId())) {
                wanters.addAll(bookWantersMap.get(l.getBookId()));
            }
            if (bookTitle != null && titleWantersMap.containsKey(bookTitle.toLowerCase().trim())) {
                wanters.addAll(titleWantersMap.get(bookTitle.toLowerCase().trim()));
            }

            for (int wanterId : wanters) {
                if (wanterId != ownerId) {
                    graph.addEdge(ownerId, wanterId, l.getId(), bookTitle);
                }
            }
        }

        return graph;
    }

    public List<MatchResult> findDirectMatches(int studentId) throws DatabaseException {
        List<MatchResult> matches = new ArrayList<>();
        List<BookListing> myListings = listingDAO.findByOwner(studentId);
        List<WishlistItem> myWishlist = wishlistDAO.getStudentWishlist(studentId);

        if (myListings.isEmpty() || myWishlist.isEmpty()) {
            return matches;
        }

        Set<Integer> wantedBookIds = new HashSet<>();
        for (WishlistItem w : myWishlist) {
            if (w.getBookId() != null) wantedBookIds.add(w.getBookId());
        }

        // Get other students' exchange listings
        List<BookListing> otherListings = listingDAO.findAvailableForExchange(studentId);
        Map<Integer, List<BookListing>> listingsByOwner = new HashMap<>();
        for (BookListing ol : otherListings) {
            listingsByOwner.computeIfAbsent(ol.getOwnerId(), k -> new ArrayList<>()).add(ol);
        }

        for (Map.Entry<Integer, List<BookListing>> entry : listingsByOwner.entrySet()) {
            int otherId = entry.getKey();
            List<BookListing> theirBooks = entry.getValue();

            // Do they have a book I want?
            BookListing theyOffer = null;
            for (BookListing tb : theirBooks) {
                if (wantedBookIds.contains(tb.getBookId())) {
                    theyOffer = tb;
                    break;
                }
            }

            if (theyOffer != null) {
                // Do they want a book I have?
                List<WishlistItem> theirWishlist = wishlistDAO.getStudentWishlist(otherId);
                Set<Integer> theyWantIds = new HashSet<>();
                for (WishlistItem tw : theirWishlist) {
                    if (tw.getBookId() != null) theyWantIds.add(tw.getBookId());
                }

                BookListing iOffer = null;
                for (BookListing mb : myListings) {
                    if (mb.getStatus() == BookListing.ListingStatus.AVAILABLE && theyWantIds.contains(mb.getBookId())) {
                        iOffer = mb;
                        break;
                    }
                }

                if (iOffer != null) {
                    MatchResult res = new MatchResult();
                    res.setMatchType("DIRECT");
                    res.setOtherStudentId(otherId);
                    res.setOtherStudentName(theyOffer.getOwnerName());
                    res.setTheyOffer(theyOffer);
                    res.setIOffer(iOffer);
                    res.setMatchScore(95.0);
                    matches.add(res);
                }
            }
        }

        return matches;
    }

    public List<ExchangeCycle> findCircularExchanges(int studentId) throws DatabaseException {
        ExchangeGraph graph = buildGraph();
        CycleDetection detector = new CycleDetection(graph, 4);
        return detector.findCyclesForStudent(studentId);
    }
}
