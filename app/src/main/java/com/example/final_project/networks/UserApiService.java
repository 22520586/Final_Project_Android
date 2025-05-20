package com.example.final_project.networks;

import com.example.final_project.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApiService {
    @POST("auth/login")
    Call<ResponseBody> login(@Body User user);

    @POST("auth/sign-up")
    Call<ResponseBody> register(@Body User user);

    @POST("auth/logout")
    Call<Void> logout();

}
