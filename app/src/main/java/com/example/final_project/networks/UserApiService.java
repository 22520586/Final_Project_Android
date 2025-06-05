package com.example.final_project.networks;

import com.example.final_project.models.User;
import com.example.final_project.models.VerifyOTP;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UserApiService {
    @POST("auth/login")
    Call<ResponseBody> login(@Body User user);

    @POST("auth/sign-up")
    Call<ResponseBody> register(@Body User user);

    @POST("auth/verify-user")
    Call<ResponseBody> verifyUser(@Body VerifyOTP verifyOTP);

    @POST("auth/logout")
    Call<Void> logout();

    @PATCH("user/update")
    Call<User> updateUser(@Body User user);

}
