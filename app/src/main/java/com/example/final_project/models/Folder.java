package com.example.final_project.models;

import com.google.gson.annotations.SerializedName;

public class Folder {
    @SerializedName("_id")
    private String id;
    private String name;
    private String userId;

    public Folder() {}

    public Folder(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}