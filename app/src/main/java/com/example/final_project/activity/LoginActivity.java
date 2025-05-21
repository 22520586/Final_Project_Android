package com.example.final_project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.models.Tokens;
import com.example.final_project.models.User;
import com.example.final_project.networks.RetrofitClient;
import com.example.final_project.networks.UserApiService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEdt, passwordEdt;
    private SharedPreferences sp;
    private Button loginBtn;
    private static final String PREFS_NAME = "SmartToken";
    private static final String KEY_TOKEN = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEdt = findViewById(R.id.emailEditText);
        passwordEdt = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginButton);

        sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdt.getText().toString();
                String password = passwordEdt.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(null,null, email, password, null);
                login(user);
            }
        });

    }

    private void login(User user) {
        UserApiService apiService = RetrofitClient.getUserApiService(this);
        Call<ResponseBody> call = apiService.login(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String json = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                        JsonObject data = jsonObject.getAsJsonObject("metadata");
                        JsonObject user = data.getAsJsonObject("user");
                        User loggedUser = new User(
                                user.get("fullname").getAsString(),
                                user.get("username").getAsString(),
                                user.get("email").getAsString(),
                                "",
                                ""
                        );
                        JsonObject tokenJs = data.getAsJsonObject("tokens");
                        Tokens tokens = new Tokens();
                        tokens.setAccessToken(tokenJs.get("accessToken").getAsString());
                        tokens.setRefreshToken(tokenJs.get("refreshToken").getAsString());
                        Log.d("Log", tokens.getAccessToken());
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(KEY_TOKEN, tokens.getAccessToken());

                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển sang MainActivity sau khi đăng nhập thành công
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    catch (Exception ex)
                    {
                        Log.e("LoginActivity", "Invalid JSON format " + ex.getMessage());
                        Toast.makeText(LoginActivity.this, "Lỗi: Dữ liệu trả về không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToSignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
