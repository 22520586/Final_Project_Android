package com.example.final_project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sp;
    private Button loginBtn;
    private static final String PREFS_NAME = "SmartToken";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "remember_me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEdt = findViewById(R.id.emailEditText);
        passwordEdt = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginBtn = findViewById(R.id.loginButton);

        sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if "Remember Me" was previously selected
        boolean isRememberMeChecked = sp.getBoolean(KEY_REMEMBER_ME, false);
        if (isRememberMeChecked) {
            String savedEmail = sp.getString(KEY_EMAIL, "");
            String savedPassword = sp.getString(KEY_PASSWORD, "");
            if (!savedEmail.isEmpty()) {
                emailEdt.setText(savedEmail);
            }
            if (!savedPassword.isEmpty()) {
                passwordEdt.setText(savedPassword);
            }
            rememberMeCheckBox.setChecked(true);
            Log.d("LoginActivity", "Restored saved credentials: email=" + savedEmail);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdt.getText().toString().trim();
                String password = passwordEdt.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(null, null, email, password, null);
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
                        Log.d("LoginActivity", "Raw JSON: " + json);
                        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                        JsonObject data = jsonObject.getAsJsonObject("metadata");
                        if (data == null) {
                            throw new Exception("Metadata is null");
                        }
                        JsonObject userJson = data.getAsJsonObject("user");
                        if (userJson == null) {
                            throw new Exception("User object is null");
                        }

                        // Safely get username with null check
                        String username = userJson.has("username") && !userJson.get("username").isJsonNull()
                                ? userJson.get("username").getAsString()
                                : "";
                        String fullname = userJson.has("fullname") && !userJson.get("fullname").isJsonNull()
                                ? userJson.get("fullname").getAsString()
                                : "";
                        String email = userJson.has("email") && !userJson.get("email").isJsonNull()
                                ? userJson.get("email").getAsString()
                                : user.getEmail();

                        User loggedUser = new User(fullname, username, email, "", "");

                        JsonObject tokenJs = data.getAsJsonObject("tokens");
                        if (tokenJs == null) {
                            throw new Exception("Tokens object is null");
                        }
                        Tokens tokens = new Tokens();
                        tokens.setAccessToken(tokenJs.has("accessToken") && !tokenJs.get("accessToken").isJsonNull()
                                ? tokenJs.get("accessToken").getAsString()
                                : "");
                        tokens.setRefreshToken(tokenJs.has("refreshToken") && !tokenJs.get("refreshToken").isJsonNull()
                                ? tokenJs.get("refreshToken").getAsString()
                                : "");

                        if (tokens.getAccessToken().isEmpty()) {
                            throw new Exception("Access token is empty");
                        }

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(KEY_TOKEN, tokens.getAccessToken());

                        // Save credentials if "Remember Me" is checked
                        if (rememberMeCheckBox.isChecked()) {
                            editor.putString(KEY_EMAIL, user.getEmail());
                            editor.putString(KEY_PASSWORD, user.getPassword());
                            editor.putBoolean(KEY_REMEMBER_ME, true);
                            Log.d("LoginActivity", "Saving credentials: email=" + user.getEmail());
                        } else {
                            editor.remove(KEY_EMAIL);
                            editor.remove(KEY_PASSWORD);
                            editor.putBoolean(KEY_REMEMBER_ME, false);
                            Log.d("LoginActivity", "Clearing saved credentials");
                        }

                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception ex) {
                        Log.e("LoginActivity", "Error parsing JSON: " + ex.getMessage(), ex);
                        Toast.makeText(LoginActivity.this, "Lỗi: Dữ liệu trả về không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("LoginActivity", "Login failed with code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LoginActivity", "Network error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToSignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}