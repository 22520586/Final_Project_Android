package com.example.final_project.networks;

import com.example.final_project.models.Document;
import com.example.final_project.requests.UpdateRequest;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DocumentApiServices {

    @GET("document/get-docs-user")
    Call<List<Document>> getAllDocuments();

    @GET("document/search")
    Call<List<Document>> searchDocuments(@Query("q") String query);

    @GET("document/{id}")
    Call<Document> getDocumentById(@Path("id") int id);

    @GET("document/extract/{id}")
    Call<JsonObject> extractText(@Path("id") String documentId);

    @GET("document/get-pinned-docs")
    Call<List<Document>> getPinnedDocuments();
    @Multipart
    @POST("document/")
    Call<ResponseBody> uploadDocument(
            @Part MultipartBody.Part document,
            @Part("title") RequestBody title,
            @Part("tags") RequestBody tags);

    @POST("document/toggle-pin/{id}")
    Call<Document> togglePin(@Path("id") String documentId);

    @DELETE("document/{id}")
    Call<ResponseBody> deleteDocument(@Path("id") String documentId);

    @PATCH("document/{id}")
    Call<ResponseBody> updateDocument(@Path("id") String documentId, @Body UpdateRequest updatedDocument);

    @GET("document/filter")
    Call<List<Document>> filterDocument(
            @Query("tags") String tags,
            @Query("fileType") String type
    );
}
