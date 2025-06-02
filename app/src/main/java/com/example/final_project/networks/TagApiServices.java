package com.example.final_project.networks;

import com.example.final_project.models.Tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TagApiServices {
    @GET("tag")
    Call<List<Tag>> getAllTags();
}
