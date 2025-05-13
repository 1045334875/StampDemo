package com.example.demo.model;

public class SeamConfiguration {
    private int seamConfigId;
    private String documentId;
    private String seamType;
    private String crossPages;

    // Constructor
    public SeamConfiguration() {
    }

    public SeamConfiguration(int seamConfigId, String documentId, String seamType, String crossPages) {
        this.seamConfigId = seamConfigId;
        this.documentId = documentId;
        this.seamType = seamType;
        this.crossPages = crossPages;
    }

    // Getters and Setters
    public int getSeamConfigId() {
        return seamConfigId;
    }

    public void setSeamConfigId(int seamConfigId) {
        this.seamConfigId = seamConfigId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSeamType() {
        return seamType;
    }

    public void setSeamType(String seamType) {
        this.seamType = seamType;
    }

    public String getCrossPages() {
        return crossPages;
    }

    public void setCrossPages(String crossPages) {
        this.crossPages = crossPages;
    }
}