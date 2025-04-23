package com.example.final_project;

import java.util.Date;

public class Document {
    private String type;
    private String title;
    private boolean isPinned;
    private String path;
    private long createdAt; // thời gian tạo (timestamp)
    private long updatedAt; // thời gian cập nhật (timestamp)

    public Document(String type, String title) {
        this.type = type;
        this.title = title;
        this.isPinned = false;
        this.path = "";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = 0;
    }

    public Document(String type, String title, String path, boolean isPinned) {
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.path = path;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = 0;
    }

    // Constructor với đầy đủ tham số
    public Document(String type, String title, String path, boolean isPinned,
                    long createdAt, long updatedAt) {
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.path = path;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        // Cập nhật thời gian chỉnh sửa khi thay đổi tiêu đề
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        // Cập nhật thời gian chỉnh sửa khi thay đổi đường dẫn
        this.updatedAt = System.currentTimeMillis();
    }

    public void togglePinned() {
        isPinned = !isPinned;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Phương thức lấy thời gian để hiển thị (ưu tiên thời gian cập nhật)
    public long getDisplayDate() {
        return updatedAt > 0 ? updatedAt : createdAt;
    }

    public String getDescription() {
        return null;
    }
}