package com.example.final_project;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileDialogHelper {

    private MainActivity activity;
    private Dialog userProfileDialog;
    private Dialog editProfileDialog;
    private Dialog changePasswordDialog;
    private Dialog logoutConfirmationDialog;

    // User data - in a real app, this would come from a database or shared preferences
    private String fullName = "Nguyễn Văn A";
    private String gender = "Nam";
    private String phone = "0912345678";
    private String email = "nguyenvana@email.com";
    private String username = "nguyenvana";

    public UserProfileDialogHelper(MainActivity activity) {
        this.activity = activity;
    }

    public void showUserProfileDialog() {
        userProfileDialog = new Dialog(activity);
        userProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        userProfileDialog.setContentView(R.layout.dialog_user_profile);
        userProfileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views
        TextView tvUserName = userProfileDialog.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = userProfileDialog.findViewById(R.id.tv_user_email);
        Button btnEditProfile = userProfileDialog.findViewById(R.id.btn_edit_profile);
        Button btnLogout = userProfileDialog.findViewById(R.id.btn_logout);
        ImageButton btnClose = userProfileDialog.findViewById(R.id.btn_close);

        // Set user data
        tvUserName.setText(fullName);
        tvUserEmail.setText(email);

        // Set click listeners
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileDialog.dismiss();
                showEditProfileDialog();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileDialog.dismiss();
            }
        });

        userProfileDialog.show();
    }

    private void showEditProfileDialog() {
        editProfileDialog = new Dialog(activity);
        editProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editProfileDialog.setContentView(R.layout.dialog_edit_profile);
        editProfileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views
        ImageButton btnClose = editProfileDialog.findViewById(R.id.btn_close_edit);
        EditText etFullName = editProfileDialog.findViewById(R.id.et_fullname);
        Spinner spinnerGender = editProfileDialog.findViewById(R.id.spinner_gender);
        EditText etPhone = editProfileDialog.findViewById(R.id.et_phone);
        EditText etEmail = editProfileDialog.findViewById(R.id.et_email);
        EditText etUsername = editProfileDialog.findViewById(R.id.et_username);
        Button btnSaveProfile = editProfileDialog.findViewById(R.id.btn_save_profile);
        Button btnChangePassword = editProfileDialog.findViewById(R.id.btn_change_password);

        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                activity,
                R.array.gender_array, // You need to create this in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Select the current gender
        int position = adapter.getPosition(gender);
        spinnerGender.setSelection(position);

        // Set current data
        etFullName.setText(fullName);
        etPhone.setText(phone);
        etEmail.setText(email);
        etUsername.setText(username);

        // Set click listeners
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileDialog.dismiss();
                showUserProfileDialog();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the changes
                fullName = etFullName.getText().toString();
                gender = spinnerGender.getSelectedItem().toString();
                phone = etPhone.getText().toString();
                email = etEmail.getText().toString();
                username = etUsername.getText().toString();

                activity.showToast("Cập nhật thông tin thành công");
                editProfileDialog.dismiss();
                showUserProfileDialog();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        editProfileDialog.show();
    }

    private void showChangePasswordDialog() {
        changePasswordDialog = new Dialog(activity);
        changePasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changePasswordDialog.setContentView(R.layout.dialog_change_password);
        changePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views
        ImageButton btnClose = changePasswordDialog.findViewById(R.id.btn_close_password);
        EditText etCurrentPassword = changePasswordDialog.findViewById(R.id.et_current_password);
        EditText etNewPassword = changePasswordDialog.findViewById(R.id.et_new_password);
        EditText etConfirmPassword = changePasswordDialog.findViewById(R.id.et_confirm_password);
        Button btnCancel = changePasswordDialog.findViewById(R.id.btn_cancel_password);
        Button btnConfirm = changePasswordDialog.findViewById(R.id.btn_confirm_password);

        // Set click listeners
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate passwords
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    activity.showToast("Vui lòng nhập đầy đủ thông tin");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    activity.showToast("Mật khẩu mới không khớp");
                    return;
                }

                // In a real app, you would verify the current password and update the new one
                activity.showToast("Đổi mật khẩu thành công");
                changePasswordDialog.dismiss();
            }
        });

        changePasswordDialog.show();
    }

    private void showLogoutConfirmationDialog() {
        logoutConfirmationDialog = new Dialog(activity);
        logoutConfirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutConfirmationDialog.setContentView(R.layout.dialog_logout_confirmation);
        logoutConfirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views
        Button btnCancel = logoutConfirmationDialog.findViewById(R.id.btn_cancel_logout);
        Button btnConfirm = logoutConfirmationDialog.findViewById(R.id.btn_confirm_logout);

        // Set click listeners
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutConfirmationDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout
                activity.showToast("Đăng xuất thành công");

                // Close all dialogs
                if (userProfileDialog != null && userProfileDialog.isShowing()) {
                    userProfileDialog.dismiss();
                }
                logoutConfirmationDialog.dismiss();

                // In a real app, you would navigate to the login screen or clear user session
            }
        });

        logoutConfirmationDialog.show();
    }
}