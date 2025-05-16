package com.example.demo.evidenceModel;
public class EvidenceResponse {
    private Auth auth;
    private String hash;
    private String signature;
    private String receiptSignature;
    private String timestamp;

    // Getters and Setters
    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getReceiptSignature() {
        return receiptSignature;
    }

    public void setReceiptSignature(String receiptSignature) {
        this.receiptSignature = receiptSignature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}