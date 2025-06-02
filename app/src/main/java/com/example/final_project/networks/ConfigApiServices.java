package com.example.final_project.networks;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConfigApiServices {
    @GET("api/config")
    Call<JsonObject> getConfig();
}
