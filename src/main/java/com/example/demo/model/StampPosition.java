package com.example.demo.model;

public class StampPosition {
    private int positionId;
    private String documentId;
    private String stampId;
    private int page;
    private float x;
    private float y;

    // Constructor
    public StampPosition() {
    }

    public StampPosition(int positionId, String documentId, String stampId, int page, float x, float y) {
        this.positionId = positionId;
        this.documentId = documentId;
        this.stampId = stampId;
        this.page = page;
        this.x = x;
        this.y = y;
    }

    // Getters and Setters
    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStampId() {
        return stampId;
    }

    public void setStampId(String stampId) {
        this.stampId = stampId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}