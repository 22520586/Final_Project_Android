package com.example.final_project.requests;

public class UpdateRequest {
    private String id;
    private String title;
    private String tags;

    public UpdateRequest(String id, String title, String tags)
    {
        this.id = id;
        this.title = title;
        this.tags = tags;
    }

    private String getId() {
        return id;
    }
    private void setId(String id)
    {
        this.id = id;
    }
}


