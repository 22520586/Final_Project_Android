package com.example.final_project.networks;

import com.example.final_project.models.ApiResponse;
import com.example.final_project.models.DeleteResult;
import com.example.final_project.models.Document;
import com.example.final_project.models.Folder;
import com.example.final_project.requests.FolderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FolderApiServices {
    @POST("folder")
    Call<ApiResponse<Folder>> createFolder(@Body FolderRequest folderRequest);

    @GET("folder")
    Call<ApiResponse<List<Folder>>> getFoldersByUserId();

    @GET("folder/{id}")
    Call<ApiResponse<List<Document>>> getDocumentsByFolderId(@Path("id") String folderId);

    @DELETE("folder/{id}")
    Call<ApiResponse<DeleteResult>> deleteFolder(@Path("id") String id);
    @PATCH("folder/{id}")
    Call<ApiResponse<Folder>> renameFolder(@Path("id") String folderId, @Body FolderRequest folderRequest);
}