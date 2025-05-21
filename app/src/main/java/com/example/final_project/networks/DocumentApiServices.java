package com.example.final_project.networks;

import com.example.final_project.models.Document;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DocumentApiServices {

    @GET("document/get-docs-user")
    Call<List<Document>> getAllDocuments();
}
