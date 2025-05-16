package com.example.demo.evidenceModel;

public class TerminationRequest {
    private String evidenceCode;
    private String tEnd;
    private String signature;

    // Getters and Setters
    public String getEvidenceCode() {
        return evidenceCode;
    }

    public void setEvidenceCode(String evidenceCode) {
        this.evidenceCode = evidenceCode;
    }

    public String getTEnd() {
        return tEnd;
    }

    public void setTEnd(String tEnd) {
        this.tEnd = tEnd;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}