package com.example.final_project.networks;

import com.example.final_project.models.Document;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DocumentApiServices {

    @GET("document/get-docs-user")
    Call<List<Document>> getAllDocuments();

    @GET("document/search")
    Call<List<Document>> searchDocuments(@Query("q") String query);

    @GET("document/:id")
    Call<Document> getDocumentById(@Path("id") int id);

    @POST("document/upload")
    Call<List<Document>> uploadDocument(@Part MultipartBody.Part file);
}
