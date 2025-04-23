package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvStorage;
    private ProgressBar storageProgressBar;
    private Switch switchNotification, switchAIAssistant, switchDataSaver;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Tham chiếu các view
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvStorage = findViewById(R.id.tvStorage);
        storageProgressBar = findViewById(R.id.storageProgressBar);
        switchNotification = findViewById(R.id.switchNotification);
        switchAIAssistant = findViewById(R.id.switchAIAssistant);
        switchDataSaver = findViewById(R.id.switchDataSaver);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Kiểm tra null
        if (tvUsername == null || tvEmail == null || tvStorage == null || storageProgressBar == null ||
                switchNotification == null || switchAIAssistant == null || switchDataSaver == null ||
                bottomNavigationView == null) {
            Toast.makeText(this, "Lỗi: Không thể hiển thị màn hình cài đặt!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo dữ liệu
        tvUsername.setText("Nguyễn Văn A");
        tvEmail.setText("nguyenvana@email.com");
        tvStorage.setText("2.4GB / 5GB (48%)");
        storageProgressBar.setProgress(48);

        // Khôi phục trạng thái switch từ SharedPreferences
        switchNotification.setChecked(sharedPreferences.getBoolean("notification_enabled", true));
        switchAIAssistant.setChecked(sharedPreferences.getBoolean("ai_assistant_enabled", true));
        switchDataSaver.setChecked(sharedPreferences.getBoolean("data_saver_enabled", false));

        // Xử lý sự kiện cho các switch
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notification_enabled", isChecked);
            editor.apply();
            Toast.makeText(this, "Thông báo: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show();
        });

        switchAIAssistant.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("ai_assistant_enabled", isChecked);
            editor.apply();
            Toast.makeText(this, "Trợ lý AI: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show();
        });

        switchDataSaver.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("data_saver_enabled", isChecked);
            editor.apply();
            Toast.makeText(this, "Tiết kiệm dữ liệu: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show();
        });

        // Thiết lập BottomNavigationView
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_folder) {
                startActivity(new Intent(SettingActivity.this, FolderActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }
}