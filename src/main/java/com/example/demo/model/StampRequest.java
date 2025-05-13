package com.example.demo.model;

import java.util.List;

public class StampRequest {
    private String userId;
    private String stampId;
    private SeamConfig seamConfig;
    private Position position;

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStampId() {
        return stampId;
    }

    public void setStampId(String stampId) {
        this.stampId = stampId;
    }

    public SeamConfig getSeamConfig() {
        return seamConfig;
    }

    public void setSeamConfig(SeamConfig seamConfig) {
        this.seamConfig = seamConfig;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public static class SeamConfig {
        private String seamType;
        private List<Integer> crossPages;

        // Getters and Setters
        public String getSeamType() {
            return seamType;
        }

        public void setSeamType(String seamType) {
            this.seamType = seamType;
        }

        public List<Integer> getCrossPages() {
            return crossPages;
        }

        public void setCrossPages(List<Integer> crossPages) {
            this.crossPages = crossPages;
        }
    }

    public static class Position {
        private List<Integer> pages;
        private float x;
        private float y;

        // Getter和Setter方法
        public List<Integer> getPages() {
            return pages;
        }

        public void setPages(List<Integer> pages) {
            this.pages = pages;
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
}