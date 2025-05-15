package com.example.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.adapters.FolderAdapter;
import com.example.final_project.helpers.AddFolderDialogHelper;
import com.example.final_project.models.Folder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FolderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FolderAdapter adapter;
    private List<Folder> folderList;
    private TextView titleText;
    private BottomNavigationView bottomNavigationView;
    private AddFolderDialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        // Khởi tạo các view
        initializeViews();

        // Khởi tạo helper
        dialogHelper = new AddFolderDialogHelper(this);

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Xử lý sự kiện các nút
        setupButtonListeners();

        // Thiết lập BottomNavigationView
        setupBottomNavigation();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.foldersRecyclerView);
        titleText = findViewById(R.id.titleText);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu giả lập (có thể thay bằng dữ liệu thực từ cơ sở dữ liệu)
        folderList = new ArrayList<>();
        folderList.add(new Folder("Công việc"));
        folderList.add(new Folder("Cá nhân"));
        folderList.add(new Folder("Học tập"));

        // Thiết lập adapter với OnFolderClickListener
        adapter = new FolderAdapter(this, folderList, folder -> {
            Intent intent = new Intent(FolderActivity.this, FolderDetailActivity.class);
            intent.putExtra("FOLDER_NAME", folder.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupButtonListeners() {
        // Xử lý sự kiện nút thêm thư mục
        FloatingActionButton addButton = findViewById(R.id.addFolderFab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.showAddFolderDialog();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                // Chuyển về MainActivity (Trang chủ)
                startActivity(new Intent(FolderActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_folder) {
                // Đã ở FolderActivity, không làm gì
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                // Chuyển sang SettingActivity
                startActivity(new Intent(FolderActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // Mặc định chọn tab Thư mục
        bottomNavigationView.setSelectedItemId(R.id.nav_folder);
    }

    // Phương thức để thêm thư mục mới
    public void addFolder(Folder folder) {
        folderList.add(folder);
        adapter.notifyDataSetChanged();
    }

    // Phương thức để hiển thị thông báo
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}