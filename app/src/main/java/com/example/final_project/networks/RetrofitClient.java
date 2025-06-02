package com.example.final_project.networks;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static String BASE_URL = "http://10.0.2.2:3052/api/v1/";
    private static DocumentApiServices documentApiServices;
    private static UserApiService userApiService;
    private static BotApiServices botApiServices;
    private static ConfigApiServices configApiServices;
    private static TagApiServices tagApiServices;
    private static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static UserApiService getUserApiService(Context context) {
        if (userApiService == null) {
            userApiService = getRetrofit(context).create(UserApiService.class);
        }
        return userApiService;
    }

    public static DocumentApiServices getDocumentApiService(Context context) {
        if (documentApiServices == null) {
            documentApiServices = getRetrofit(context).create(DocumentApiServices.class);
        }
        return documentApiServices;
    }

    public static BotApiServices getBotApiService(Context context) {
        if (botApiServices == null) {
            botApiServices = getRetrofit(context).create(BotApiServices.class);
        }
        return botApiServices;
    }

    public static ConfigApiServices getConfigApiService(Context context) {
        if (configApiServices == null) {
            configApiServices = getRetrofit(context).create(ConfigApiServices.class);
        }
        return configApiServices;
    }

    public static TagApiServices getTagApiService(Context context) {
        if (tagApiServices == null) {
            tagApiServices = getRetrofit(context).create(TagApiServices.class);
        }
        return tagApiServices;
    }

}
