package com.example.final_project.networks;

import com.example.final_project.models.TTSReponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TTSApiServices {
    @Headers("Content-Type: text/plain")
    @POST("tts/v5")
    Call<TTSReponse> convertTextToSpeech(
            @Body String text,
            @Header("api_key") String apiKey
    );
}
