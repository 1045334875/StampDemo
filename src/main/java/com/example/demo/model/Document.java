package com.example.demo.model;

public class Document {
    private String documentId;
    private String userId;
    private String documentName;
    private byte[] documentFile;

    // Constructor
    public Document() {
    }

    public Document(String documentId, String userId, String documentName, byte[] documentFile) {
        this.documentId = documentId;
        this.userId = userId;
        this.documentName = documentName;
        this.documentFile = documentFile;
    }

    // Getters and Setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public byte[] getDocumentFile() {
        return documentFile;
    }

    public void setDocumentFile(byte[] documentFile) {
        this.documentFile = documentFile;
    }
}