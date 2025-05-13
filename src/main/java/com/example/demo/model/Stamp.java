package com.example.demo.model;

import java.util.UUID;

public class Stamp {
    private String stampId;
    private String userId;
    private String style;
    private String color;
    private String wrapText;
    private String horizonText;
    private String handwritten;
    private String stampImage;
    private boolean isDefault;
    // 构造函数
    public Stamp() {
        this.stampId = UUID.randomUUID().toString();
    }

//    // 枚举类型定义签章样式
//    public enum Style {
//        OFFICIAL, SPECIAL, OVAL, SQUARE
//    }
//
//    // 枚举类型定义签章颜色
//    public enum Color {
//        RED, BLUE, BLACK
//    }

    // Getters and Setters
    public String getStampId() {
        return stampId;
    }

    public void setStampId(String stampId) {
        this.stampId = stampId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStyle() { return style;  }

    public void setStyle(String style) {
        this.style = style.toUpperCase();
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color.toUpperCase();
    }

    public String getWrapText() {
        return wrapText;
    }

    public void setWrapText(String wrapText) {
        this.wrapText = wrapText;
    }

    public String getHorizonText() {
        return horizonText;
    }

    public void setHorizonText(String horizonText) {
        this.horizonText = horizonText;
    }

    public String getHandwritten() {
        return handwritten;
    }

    public void setHandwritten(String handwritten) {
        this.handwritten = handwritten;
    }

    public String getStampImage() {
        return stampImage;
    }

    public void setStampImage(String stampImage) {
        this.stampImage = stampImage;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}