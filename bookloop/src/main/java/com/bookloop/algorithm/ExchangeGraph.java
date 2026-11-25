package com.bookloop.algorithm;

import java.util.*;

/**
 * Directed exchange graph for detecting direct and circular student exchanges.
 * - Nodes: Student IDs
 * - Directed Edge (A -> B): Student A owns an AVAILABLE book that Student B wants in their wishlist.
 */
public class ExchangeGraph {
    public static class Edge {
        private final int toStudentId;
        private final int listingId;
        private final String bookTitle;

        public Edge(int toStudentId, int listingId, String bookTitle) {
            this.toStudentId = toStudentId;
            this.listingId = listingId;
            this.bookTitle = bookTitle;
        }

        public int getToStudentId() { return toStudentId; }
        public int getListingId() { return listingId; }
        public String getBookTitle() { return bookTitle; }
    }

    private final Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
    private final Map<Integer, String> studentNames = new HashMap<>();

    public void addStudent(int studentId, String name) {
        adjacencyList.putIfAbsent(studentId, new ArrayList<>());
        if (name != null && !name.isEmpty()) {
            studentNames.put(studentId, name);
        }
    }

    public void addEdge(int fromStudentId, int toStudentId, int listingId, String bookTitle) {
        adjacencyList.computeIfAbsent(fromStudentId, k -> new ArrayList<>())
                     .add(new Edge(toStudentId, listingId, bookTitle));
    }

    public Set<Integer> getAllStudents() {
        return adjacencyList.keySet();
    }

    public List<Edge> getNeighbors(int studentId) {
        return adjacencyList.getOrDefault(studentId, Collections.emptyList());
    }

    public String getStudentName(int studentId) {
        return studentNames.getOrDefault(studentId, "Student #" + studentId);
    }

    public int size() {
        return adjacencyList.size();
    }
}
