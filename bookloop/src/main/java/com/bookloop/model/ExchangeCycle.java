package com.bookloop.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExchangeCycle {
    public enum CycleStatus { PROPOSED, PARTIAL_ACCEPT, ALL_ACCEPTED, COMPLETED, CANCELLED, EXPIRED }

    private int id;
    private String cycleHash;
    private int participantCount;
    private CycleStatus status;
    private int initiatedBy;
    private String initiatorName;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<Integer> participantIds;
    private List<Step> steps;
    private int length;

    public static class Step {
        private int id;
        private int studentId;
        private String studentName;
        private int givesListingId;
        private String givesBookTitle;
        private int receivesListingId;
        private String receivesBookTitle;
        private int positionOrder;
        private boolean confirmed;
        private LocalDateTime confirmedAt;

        // Legacy / algorithm mapping helper fields
        private int giverId;
        private String giverName;
        private int receiverId;
        private String receiverName;
        private int listingId;
        private String bookTitle;

        public Step() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getStudentId() { return studentId != 0 ? studentId : giverId; }
        public void setStudentId(int studentId) { this.studentId = studentId; this.giverId = studentId; }

        public String getStudentName() { return studentName != null ? studentName : giverName; }
        public void setStudentName(String studentName) { this.studentName = studentName; this.giverName = studentName; }

        public int getGivesListingId() { return givesListingId != 0 ? givesListingId : listingId; }
        public void setGivesListingId(int givesListingId) { this.givesListingId = givesListingId; this.listingId = givesListingId; }

        public String getGivesBookTitle() { return givesBookTitle != null ? givesBookTitle : bookTitle; }
        public void setGivesBookTitle(String givesBookTitle) { this.givesBookTitle = givesBookTitle; this.bookTitle = givesBookTitle; }

        public int getReceivesListingId() { return receivesListingId; }
        public void setReceivesListingId(int receivesListingId) { this.receivesListingId = receivesListingId; }

        public String getReceivesBookTitle() { return receivesBookTitle; }
        public void setReceivesBookTitle(String receivesBookTitle) { this.receivesBookTitle = receivesBookTitle; }

        public int getPositionOrder() { return positionOrder; }
        public void setPositionOrder(int positionOrder) { this.positionOrder = positionOrder; }

        public boolean isConfirmed() { return confirmed; }
        public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

        public LocalDateTime getConfirmedAt() { return confirmedAt; }
        public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

        public int getGiverId() { return getStudentId(); }
        public void setGiverId(int giverId) { setStudentId(giverId); }

        public String getGiverName() { return getStudentName(); }
        public void setGiverName(String giverName) { setStudentName(giverName); }

        public int getReceiverId() { return receiverId; }
        public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

        public int getListingId() { return getGivesListingId(); }
        public void setListingId(int listingId) { setGivesListingId(listingId); }

        public String getBookTitle() { return getGivesBookTitle(); }
        public void setBookTitle(String bookTitle) { setGivesBookTitle(bookTitle); }
    }

    public ExchangeCycle() {
        this.status = CycleStatus.PROPOSED;
        this.participantIds = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCycleHash() { return cycleHash; }
    public void setCycleHash(String cycleHash) { this.cycleHash = cycleHash; }

    public int getParticipantCount() { return participantCount > 0 ? participantCount : (steps != null ? steps.size() : 0); }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    public CycleStatus getStatus() { return status; }
    public void setStatus(CycleStatus status) { this.status = status; }

    public int getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(int initiatedBy) { this.initiatedBy = initiatedBy; }

    public String getInitiatorName() { return initiatorName; }
    public void setInitiatorName(String initiatorName) { this.initiatorName = initiatorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<Integer> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Integer> participantIds) { this.participantIds = participantIds; }

    public List<Step> getSteps() { return steps; }
    public void setSteps(List<Step> steps) {
        this.steps = steps;
        this.participantCount = steps != null ? steps.size() : 0;
    }

    public int getLength() { return length > 0 ? length : (steps != null ? steps.size() : 0); }
    public void setLength(int length) { this.length = length; }
}
