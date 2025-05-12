package com.example.final_project.networks;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static String BASE_URL = "http://10.0.2.2:3052/api/v1";
    private static DocumentApiServices documentApiServices;
    private static UserApiService userApiService;
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static UserApiService getUserApiService() {
        if (userApiService == null) {
            userApiService = getRetrofit().create(UserApiService.class);
        }
        return userApiService;
    }

    public static DocumentApiServices getDocumentApiService() {
        if (documentApiServices == null) {
            documentApiServices = getRetrofit().create(DocumentApiServices.class);
        }
        return documentApiServices;
    }

}
