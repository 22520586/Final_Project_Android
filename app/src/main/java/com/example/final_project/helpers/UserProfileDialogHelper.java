package com.example.final_project.helpers;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.activity.LoginActivity;
import com.example.final_project.models.User;
import com.example.final_project.networks.RetrofitClient;
import com.example.final_project.networks.UserApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileDialogHelper {

    private MainActivity activity;
    private Dialog userProfileDialog;
    private Dialog editProfileDialog;
    private Dialog changePasswordDialog;
    private Dialog logoutConfirmationDialog;

    // User data
    private String fullName;
    private String gender;
    private String phone;
    private String email;
    private String username;
    private User user;

    public UserProfileDialogHelper(MainActivity activity, User user) {
        this.activity = activity;
        this.user = user;
        // Initialize user data from User object
        if (user != null) {
            this.fullName = user.getName();
            this.phone = user.getPhone();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.gender = "Nam"; // Default value, as gender is not in User model
        }
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

        if (user == null) {
            tvUserName.setText("Khách");
            tvUserEmail.setText("Không có email");
        } else {
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
        }

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
        EditText etPhone = editProfileDialog.findViewById(R.id.et_phone);
        EditText etEmail = editProfileDialog.findViewById(R.id.et_email);
        EditText etUsername = editProfileDialog.findViewById(R.id.et_username);
        Button btnSaveProfile = editProfileDialog.findViewById(R.id.btn_save_profile);
        Button btnChangePassword = editProfileDialog.findViewById(R.id.btn_change_password);

        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                activity,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
                // Get input data
                final String inputFullName = etFullName.getText().toString().trim();
                final String inputPhone = etPhone.getText().toString().trim();
                final String inputEmail = etEmail.getText().toString().trim();
                final String inputUsername = etUsername.getText().toString().trim();

                // Use existing values if inputs are empty
                String finalFullName = inputFullName.isEmpty() ? user.getName() : inputFullName;
                String finalPhone = inputPhone.isEmpty() ? user.getPhone() : inputPhone;
                String finalEmail = inputEmail.isEmpty() ? user.getEmail() : inputEmail;
                String finalUsername = inputUsername.isEmpty() ? user.getUsername() : inputUsername;

                // Log payload
                Log.d("UpdateUser", "Sending payload: fullname=" + finalFullName + ", username=" + finalUsername + ", email=" + finalEmail + ", phone=" + finalPhone);

                // Create User object for API call
                User updatedUser = new User(finalFullName, finalUsername, finalEmail, null, finalPhone); // Loại bỏ password

                // Call API to update user
                UserApiService userApiService = RetrofitClient.getUserApiService(activity);
                Call<User> call = userApiService.updateUser(updatedUser);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Update local data
                            fullName = finalFullName;
                            phone = finalPhone;
                            email = finalEmail;
                            username = finalUsername;
                            user.setName(finalFullName);
                            user.setPhone(finalPhone);
                            user.setEmail(finalEmail);
                            user.setUsername(finalUsername);

                            activity.showToast("Cập nhật thông tin thành công");
                            editProfileDialog.dismiss();
                            showUserProfileDialog();
                        } else {
                            String errorMessage = "Cập nhật thông tin thất bại. Mã lỗi: " + response.code();
                            try {
                                errorMessage += "\nChi tiết: " + response.errorBody().string();
                            } catch (Exception e) {
                                errorMessage += "\nKhông thể đọc chi tiết lỗi: " + e.getMessage();
                            }
                            activity.showToast(errorMessage);
                            Log.e("UpdateUser", errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        if (t.getMessage() != null && t.getMessage().contains("Unauthorized - Redirecting to Login")) {
                            errorMessage = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                        }
                        activity.showToast(errorMessage);
                        Log.e("UpdateUser", errorMessage, t);
                    }
                });
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
                // Perform logout API call
                UserApiService userApiService = RetrofitClient.getUserApiService(activity);
                Call<Void> call = userApiService.logout();
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            activity.showToast("Đăng xuất thành công");

                            // Close all dialogs
                            if (userProfileDialog != null && userProfileDialog.isShowing()) {
                                userProfileDialog.dismiss();
                            }
                            logoutConfirmationDialog.dismiss();

                            // Clear user session
                            clearUserSession();

                            // Navigate to login screen
                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            String errorMessage = "Đăng xuất thất bại. Mã lỗi: " + response.code();
                            try {
                                errorMessage += "\nChi tiết: " + response.errorBody().string();
                            } catch (Exception e) {
                                errorMessage += "\nKhông thể đọc chi tiết lỗi: " + e.getMessage();
                            }
                            activity.showToast(errorMessage);
                            Log.e("LogoutUser", errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        if (t.getMessage() != null && t.getMessage().contains("Unauthorized - Redirecting to Login")) {
                            errorMessage = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                        }
                        activity.showToast(errorMessage);
                        Log.e("LogoutUser", errorMessage, t);
                    }
                });
            }
        });

        logoutConfirmationDialog.show();
    }

    private void clearUserSession() {
        SharedPreferences prefs = activity.getSharedPreferences("SmartToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("token"); // Xóa token
        editor.apply(); // Sử dụng apply() thay vì clear() để chỉ xóa token
    }
}