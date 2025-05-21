package com.example.final_project.networks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.final_project.activity.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SharedPreferences sharedPreferences;
    private final Context context;
    private static final String PREFS_NAME = "SmartToken";
    private static final String KEY_TOKEN = "token";
    public AuthInterceptor(Context context)  {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request orginRequest = chain.request();
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        Log.d("AuthInterceptor", "Authorization header: " + token);

        Request.Builder builder = orginRequest.newBuilder();
        if (token != null && !orginRequest.url().toString().contains("login") && !orginRequest.url().toString().contains("sign-up")) {
            builder.addHeader("authorization", token);

        }

        Request newRequest = builder.build();
        Response response = chain.proceed(newRequest);

        if (response.code() == 401) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(KEY_TOKEN);
            editor.apply();

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent); // Sử dụng field context

            response.close();
            throw new IOException("Unauthorized - Redirecting to Login");
        }

        return response;
    }
}
