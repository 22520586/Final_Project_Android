package com.example.final_project.requests;

import com.google.gson.annotations.SerializedName;

public class AddToFolderRequest {
    @SerializedName("folderId")
    private final String folderId;

    public AddToFolderRequest(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderId() {
        return folderId;
    }
}