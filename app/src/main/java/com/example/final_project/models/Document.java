package com.example.final_project.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Document {
    @SerializedName("_id")
    private String id;

    @SerializedName("fileType")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("tags")
    private ArrayList<String> tags;

    @SerializedName("isPinned")
    private boolean isPinned;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("folderId")
    private String folderId; // Thêm trường folderId để ánh xạ từ API

    private String path;

    @SerializedName("createdAt")
    private String createdAt; // thời gian tạo (timestamp)

    @SerializedName("updatedAt")
    private String updatedAt; // thời gian cập nhật (timestamp)

    // Constructor cơ bản
    public Document(String title, ArrayList<String> tags) {
        this.tags = tags;
        this.title = title;
    }

    // Constructor với type, title, tags
    public Document(String type, String title, ArrayList<String> tags) {
        this.type = type;
        this.title = title;
        this.tags = tags;
    }

    // Constructor với type, title, fileUrl, isPinned, tags
    public Document(String type, String title, String fileUrl, boolean isPinned, ArrayList<String> tags) {
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.tags = tags;
        this.fileUrl = fileUrl;
    }

    // Constructor với id, type, title, path, isPinned, createdAt, updatedAt
    public Document(String id, String type, String title, String path, boolean isPinned,
                    long createdAt, long updatedAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.isPinned = isPinned;
        this.path = path;
    }

    // Getters
    public String getId() {
        return id;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return fileUrl;
    }

    public String getFolderId() {
        return folderId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public String getPath() {
        return path;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDescription() {
        return null;
    }

    // Setters
    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPinned(boolean pinned) {
        this.isPinned = pinned;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void togglePinned() {
        this.isPinned = !this.isPinned;
    }
}