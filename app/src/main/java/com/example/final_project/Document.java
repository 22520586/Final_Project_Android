package com.example.final_project;

public class Document {
    private String type;
    private String title;
    private boolean isPinned;
    private String date;

    public Document(String type, String title) {
        this.type = type;
        this.title = title;
        this.isPinned = false;
        this.date = "20/04/2025"; // Mặc định hoặc tính toán dựa trên ngày hiện tại
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}