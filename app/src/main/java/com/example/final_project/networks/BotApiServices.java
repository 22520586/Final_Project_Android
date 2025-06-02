package com.example.final_project.networks;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BotApiServices {
    @POST("bot/summarize/{id}")
    Call<JsonObject> summarizeDocument(@Path("id") String documentId);
    @POST("bot/semantic-search/{id}")
    Call<JsonObject> semanticSearch(@Body JsonObject question, @Path("id") String documentId);

}
