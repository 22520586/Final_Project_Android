package com.example.final_project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileDialog extends Dialog {

    private String userName;
    private String userEmail;

    public UserProfileDialog(Context context, String userName, String userEmail) {
        super(context);
        this.userName = userName;
        this.userEmail = userEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_user_profile);

        // Initialize views
        TextView tvUserName = findViewById(R.id.tv_user_name);
        TextView tvUserEmail = findViewById(R.id.tv_user_email);
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        Button btnLogout = findViewById(R.id.btn_logout);
        ImageButton btnClose = findViewById(R.id.btn_close);

        // Set user data
        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);

        // Set click listeners
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show();
            // Add your edit profile logic here
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đăng xuất", Toast.LENGTH_SHORT).show();
            // Add your logout logic here
            dismiss();
        });

        btnClose.setOnClickListener(v -> dismiss());
    }
}