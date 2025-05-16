package com.example.demo.evidenceModel;

public class ReceiptRequest {
    private String evidenceCode;
    private String signature;

    // Getters and Setters
    public String getEvidenceCode() {
        return evidenceCode;
    }

    public void setEvidenceCode(String evidenceCode) {
        this.evidenceCode = evidenceCode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}