package com.example.final_project.networks;

import com.example.final_project.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApiService {
    @POST("/auth/login")
    Call<User> login(@Body User user);

    @POST("/auth/sign-up")
    Call<User> register(@Body User user);

}
