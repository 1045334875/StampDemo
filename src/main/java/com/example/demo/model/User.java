package com.example.demo.model;
public class User {

    private String userId;
    private String userName;

    public User() {}

    public User(String userId) {
        this.userId = userId;
        this.userName = "default name";
    }

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}