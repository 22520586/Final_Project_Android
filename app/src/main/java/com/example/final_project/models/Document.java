package com.example.final_project.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Document {
    @SerializedName("_id")
    private String id;
    @SerializedName("fileType")
    private String type;
    private String title;

    private ArrayList<String> tags;
    private boolean isPinned;
    private String fileUrl;
    private String path;
    private String createdAt; // thời gian tạo (timestamp)
    private String updatedAt; // thời gian cập nhật (timestamp)

    public Document(String title, ArrayList<String> tags)
    {
        this.tags = tags;
        this.title = title;
    }

    public Document(String type, String title, ArrayList<String> tags) {
        this.type = type;
        this.title = title;
        this.tags = tags;
    }

    public Document(String type, String title, String fileUrl, boolean isPinned, ArrayList<String> tags) {
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.tags = tags;
        this.fileUrl = fileUrl;
    }

    public Document(String id, String type, String title, String path, boolean isPinned,
                    long createdAt, long updatedAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.path = path;
    }

    public String getId() {return id;}
    public List<String> getTags() {
        return tags;
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

    public String getUrl() {return fileUrl; }

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