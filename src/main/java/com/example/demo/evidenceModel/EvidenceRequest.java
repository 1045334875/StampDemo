package com.example.demo.evidenceModel;

public class EvidenceRequest {
    private Auth auth;
    private String hash;
    private String signature;

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
}

