package com.example.final_project;

public class Document {
    private String type;
    private String title;

    public Document(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
