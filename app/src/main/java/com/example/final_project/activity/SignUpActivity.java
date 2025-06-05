package com.example.final_project.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.models.User;
import com.example.final_project.models.VerifyOTP;
import com.example.final_project.networks.RetrofitClient;
import com.example.final_project.networks.UserApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText, emailEditText, passwordEditText, cfPasswordEditText, phoneEditText;
    private Button signUpButton;

    private UserApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        cfPasswordEditText = findViewById(R.id.cfpasswordEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        signUpButton = findViewById(R.id.signUpButton);

        // Set up SignUp button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = cfPasswordEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                apiService = RetrofitClient.getUserApiService(SignUpActivity.this);
                // Basic validation
                if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(name, username, email, password, phone);
                signUp(user);
            }
        });
    }

    private void signUp(User user) {
        Call<ResponseBody> call = apiService.register(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Toast.makeText(SignUpActivity.this, "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                    showOtpDialog(user);
                    //startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                    //finish();
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Đăng kí thất bại!", Toast.LENGTH_SHORT).show();
                    Log.d("Sign up error", response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOtpDialog(User user) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.otp_dialog);
        dialog.setCancelable(false);

        EditText otpEditText = dialog.findViewById(R.id.otpEditText);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmOtp);
        Button btnCancel = dialog.findViewById(R.id.btnCancelOtp);
        TextView resendOtpLink = dialog.findViewById(R.id.resendOtpLink);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.length() == 6) {
                verifyOtp(user, otp, dialog);
            } else {
                otpEditText.setError("Mã OTP phải gồm 6 số");
            }
        });


        dialog.show();
    }

    private void verifyOtp(User user, String otp, Dialog dialog) {
        VerifyOTP verifyOTP = new VerifyOTP(user.getEmail(), otp);
        Log.d("OTP_VERIFY", "Bắt đầu xác thực OTP");

        Call<ResponseBody> call = apiService.verifyUser(verifyOTP);
        Log.d("OTP_VERIFY", "Ở đây");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("OTP_VERIFY", "Xác thực OTP kết thúc: " + otp);
                if(response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Mã OTP không hợp lệ!", Toast.LENGTH_SHORT).show();
                    Log.d("OTP_VERIFY", "Lỗi xác thực OTP: " + response.message() + " " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to navigate to LoginActivity
    public void goToLogin(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
