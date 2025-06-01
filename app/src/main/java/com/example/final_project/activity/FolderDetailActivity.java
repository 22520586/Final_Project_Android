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
import com.example.final_project.models.ApiResponse;
import com.example.final_project.models.Document;
import com.example.final_project.networks.FolderApiServices;
import com.example.final_project.networks.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FolderDetailActivity extends AppCompatActivity {
    private TextView folderTitle;
    private ImageButton backButton;
    private RecyclerView documentsRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private DocumentAdapter adapter;
    private List<Document> documentList;
    private FolderApiServices apiServices;
    private String folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);

        folderTitle = findViewById(R.id.folderTitle);
        backButton = findViewById(R.id.backButton);
        documentsRecyclerView = findViewById(R.id.documentsRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        apiServices = RetrofitClient.getFolderApiService(this);
        documentList = new ArrayList<>();
        adapter = new DocumentAdapter(this, documentList);
        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentsRecyclerView.setAdapter(adapter);

        folderId = getIntent().getStringExtra("FOLDER_ID");
        String title = getIntent().getStringExtra("FOLDER_NAME");
        if (title != null) {
            folderTitle.setText(title);
        }
        if (folderId == null) {
            Toast.makeText(this, "Không tìm thấy ID thư mục", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchDocumentsByFolderId();

        backButton.setOnClickListener(v -> onBackPressed());
        setupBottomNavigation();
    }

    private void fetchDocumentsByFolderId() {
        Call<ApiResponse<List<Document>>> call = apiServices.getDocumentsByFolderId(folderId);
        call.enqueue(new Callback<ApiResponse<List<Document>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Document>>> call, Response<ApiResponse<List<Document>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    documentList.clear();
                    documentList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    if (documentList.isEmpty()) {
                        Toast.makeText(FolderDetailActivity.this, "Thư mục trống", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi khi tải tài liệu: " + response.code();
                    if (response.body() != null) {
                        errorMsg = response.body().getMessage() != null ? response.body().getMessage() : errorMsg;
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg += " - Không thể đọc lỗi";
                        }
                    }
                    Toast.makeText(FolderDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Document>>> call, Throwable t) {
                Toast.makeText(FolderDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(FolderDetailActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_folder) {
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(FolderDetailActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_folder);
    }
}