package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.activity.FolderActivity;
import com.example.final_project.activity.SettingActivity;
import com.example.final_project.adapters.DocumentAdapter;
import com.example.final_project.helpers.AddDocumentDialogHelper;
import com.example.final_project.helpers.DropdownMenuHelper;
import com.example.final_project.helpers.UserProfileDialogHelper;
import com.example.final_project.managers.DocumentManager;
import com.example.final_project.models.Document;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<Document> documentList;
    private ImageButton favoriteButton;
    private TextView titleText;
    private ImageButton menuButton;
    private EditText searchEditText;
    private ImageView userIconView;
    private BottomNavigationView bottomNavigationView;

    private DocumentManager documentManager;
    private AddDocumentDialogHelper dialogHelper;
    private DropdownMenuHelper dropdownMenuHelper;
    private UserProfileDialogHelper userProfileDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các view
        initializeViews();

        // Khởi tạo các helper
        documentManager = new DocumentManager(this);
        dialogHelper = new AddDocumentDialogHelper(this);
        dropdownMenuHelper = new DropdownMenuHelper(this);
        userProfileDialogHelper = new UserProfileDialogHelper(this);

        // Thiết lập RecyclerView
        setupRecyclerView();

        fetchDocuments();

        // Xử lý sự kiện các nút
        setupButtonListeners();

        // Thiết lập BottomNavigationView
        setupBottomNavigation();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.documentsRecyclerView);
        favoriteButton = findViewById(R.id.favoriteButton);
        titleText = findViewById(R.id.titleText);
        menuButton = findViewById(R.id.menuButton);
        searchEditText = findViewById(R.id.searchEditText);
        userIconView = findViewById(R.id.profileButton);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy dữ liệu tài liệu từ DocumentManager
        documentList = new ArrayList<>();

        // Thiết lập adapter
        adapter = new DocumentAdapter(this, documentList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchDocuments()
    {
        DocumentApiServices apiServices = RetrofitClient.getDocumentApiService();
        Call<List<Document>> call = apiServices.getAllDocuments();
        call.enqueue(new Callback<List<Document>>() {
            @Override
            public void onResponse(Call<List<Document>> call, Response<List<Document>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    documentList.clear();
                    documentList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    showToast("Lỗi khi tải tài liệu: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Document>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
                Log.e("MainActivity", "Lỗi kết nối: " + t.getMessage(), t);
            }
        });
    }

    private void setupButtonListeners() {
        // Xử lý sự kiện nút thêm
        FloatingActionButton addButton = findViewById(R.id.addDocumentFab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.showAddDocumentDialog();
            }
        });

        // Xử lý sự kiện nút sao
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePinnedDocuments();
            }
        });

        // Xử lý sự kiện nút menu
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropdownMenuHelper.showDropdownMenu();
            }
        });

        // Xử lý sự kiện nút user icon
        userIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileDialogHelper.showUserProfileDialog();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                // Đã ở MainActivity (Trang chủ), không cần làm gì
                return true;
            } else if (item.getItemId() == R.id.nav_folder) {
                // Chuyển sang FolderActivity
                startActivity(new Intent(MainActivity.this, FolderActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                // Chuyển sang SettingActivity
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // Mặc định chọn tab Trang chủ
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void togglePinnedDocuments() {
        if (adapter.isShowingPinned()) {
            // Nếu đang hiển thị tài liệu ghim, chuyển về hiển thị tất cả
            adapter.showAllDocuments();
            titleText.setText("Quản lý Tài liệu");
        } else {
            // Nếu đang hiển thị tất cả, chuyển sang hiển thị tài liệu ghim
            adapter.showPinnedDocuments();
            titleText.setText("Tài liệu đã ghim");
        }
    }

    // Getter để các helper có thể truy cập
    public DocumentAdapter getAdapter() {
        return adapter;
    }

    public TextView getTitleText() {
        return titleText;
    }

    // Phương thức để hiển thị thông báo từ các helper
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}