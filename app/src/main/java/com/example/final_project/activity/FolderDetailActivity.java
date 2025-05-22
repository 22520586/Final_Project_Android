package com.example.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.adapters.DocumentAdapter;
import com.example.final_project.helpers.AddDocumentDialogHelper;
import com.example.final_project.models.Document;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FolderDetailActivity extends AppCompatActivity {

    private TextView folderTitle;
    private ImageButton backButton;
    private RecyclerView documentsRecyclerView;
    private FloatingActionButton addDocumentFab;
    private BottomNavigationView bottomNavigationView; // Biến cho BottomNavigationView

    private DocumentAdapter adapter;
    private List<Document> documentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);

        // Khởi tạo các view
        folderTitle = findViewById(R.id.folderTitle);
        backButton = findViewById(R.id.backButton);
        documentsRecyclerView = findViewById(R.id.documentsRecyclerView);
        addDocumentFab = findViewById(R.id.addDocumentFab);
        bottomNavigationView = findViewById(R.id.bottomNavigation); // Sửa ID để khớp với XML

        // Lấy tên thư mục từ Intent
        String title = getIntent().getStringExtra("FOLDER_NAME");
        if (title != null) {
            folderTitle.setText(title);
        }

        // Xử lý nút quay lại
        backButton.setOnClickListener(v -> onBackPressed());

        // Danh sách tài liệu mẫu
        documentList = new ArrayList<>();
        documentList.add(new Document("pdf", "Tài liệu 1"));
        documentList.add(new Document("docx", "Tài liệu 2"));
        documentList.add(new Document("txt", "Tài liệu 3"));

        // Thiết lập RecyclerView
        adapter = new DocumentAdapter(FolderDetailActivity.this, documentList);
        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentsRecyclerView.setAdapter(adapter);

        // Nút thêm tài liệu
        addDocumentFab.setOnClickListener(view -> {
            AddDocumentDialogHelper dialogHelper = new AddDocumentDialogHelper(FolderDetailActivity.this);
            dialogHelper.showAddDocumentDialog();
        });

        // Thiết lập BottomNavigationView
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                // Chuyển về MainActivity
                startActivity(new Intent(FolderDetailActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_folder) {
                // Ở lại FolderDetailActivity, không làm gì
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                // Chuyển sang SettingActivity
                startActivity(new Intent(FolderDetailActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // Mặc định chọn tab Thư mục
        bottomNavigationView.setSelectedItemId(R.id.nav_folder);
    }
}