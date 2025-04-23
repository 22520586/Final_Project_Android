package com.example.final_project;

public class Document {
    private String type;
    private String title;
    private boolean isPinned;
    private String path;

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
}