package com.example.final_project.models;

import com.google.gson.annotations.SerializedName;

public class Document {
    @SerializedName("fileType")
    private String type;
    private String title;
    private boolean isPinned;
    private String path;
    private String createdAt; // thời gian tạo (timestamp)
    private String updatedAt; // thời gian cập nhật (timestamp)

    public Document(String type, String title) {
        this.type = type;
        this.title = title;
        this.isPinned = false;
        this.path = "";
    }

    public Document(String type, String title, String path, boolean isPinned) {
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.path = path;
    }

    // Constructor với đầy đủ tham số
    public Document(String type, String title, String path, boolean isPinned,
                    long createdAt, long updatedAt) {
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.path = path;
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
    }

    public void togglePinned() {
        isPinned = !isPinned;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Phương thức lấy thời gian để hiển thị (ưu tiên thời gian cập nhật)


    public String getDescription() {
        return null;
    }
}