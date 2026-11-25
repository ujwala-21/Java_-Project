package com.bookloop.model;

public class MatchResult {
    private String matchType; // "DIRECT" or "CIRCULAR"
    private int otherStudentId;
    private String otherStudentName;
    private BookListing theyOffer;
    private BookListing iOffer;
    private ExchangeCycle cycle;
    private double matchScore;

    public MatchResult() {}

    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }

    public int getOtherStudentId() { return otherStudentId; }
    public void setOtherStudentId(int otherStudentId) { this.otherStudentId = otherStudentId; }

    public String getOtherStudentName() { return otherStudentName; }
    public void setOtherStudentName(String otherStudentName) { this.otherStudentName = otherStudentName; }

    public BookListing getTheyOffer() { return theyOffer; }
    public void setTheyOffer(BookListing theyOffer) { this.theyOffer = theyOffer; }

    public BookListing getIOffer() { return iOffer; }
    public void setIOffer(BookListing iOffer) { this.iOffer = iOffer; }

    public ExchangeCycle getCycle() { return cycle; }
    public void setCycle(ExchangeCycle cycle) { this.cycle = cycle; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
}
