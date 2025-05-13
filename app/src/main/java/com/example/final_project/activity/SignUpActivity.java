package com.example.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText, emailEditText, passwordEditText, cfPasswordEditText;
    private Button signUpButton;

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

                // Placeholder for registration logic (e.g., save to database or send to server)
                Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                // Optionally, navigate to LoginActivity after successful signup
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to navigate to LoginActivity
    public void goToLogin(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
