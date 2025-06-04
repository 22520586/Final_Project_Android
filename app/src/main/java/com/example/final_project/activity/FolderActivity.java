package com.example.final_project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.adapters.FolderAdapter;
import com.example.final_project.helpers.AddFolderDialogHelper;
import com.example.final_project.models.ApiResponse;
import com.example.final_project.models.DeleteResult;
import com.example.final_project.models.Folder;
import com.example.final_project.requests.FolderRequest;
import com.example.final_project.networks.FolderApiServices;
import com.example.final_project.networks.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FolderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FolderAdapter adapter;
    private List<Folder> folderList;
    private TextView titleText;
    private BottomNavigationView bottomNavigationView;
    private AddFolderDialogHelper dialogHelper;
    private FolderApiServices folderApiServices;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        // Retrieve JWT token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SmartToken", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            showToast("User not logged in");
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }
        userId = extractUserIdFromToken(token);
        if (userId == null) {
            showToast("Invalid token");
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // Initialize views
        initializeViews();

        // Initialize helper and API service
        dialogHelper = new AddFolderDialogHelper(this);
        folderApiServices = RetrofitClient.getFolderApiService(this);

        // Set up RecyclerView
        setupRecyclerView();

        // Set up button listeners
        setupButtonListeners();

        // Set up BottomNavigationView
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh folders when activity resumes
        fetchFolders();
    }

    private String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
            String userId = null;
            if (payload.contains("\"userId\":\"")) {
                userId = payload.split("\"userId\":\"")[1].split("\"")[0];
            } else if (payload.contains("\"sub\":\"")) {
                userId = payload.split("\"sub\":\"")[1].split("\"")[0];
            }
            Log.d("API", "Extracted userId: " + userId);
            return userId;
        } catch (Exception e) {
            Log.e("API", "Error decoding token: " + e.getMessage());
            return null;
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.foldersRecyclerView);
        titleText = findViewById(R.id.titleText);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerView() {
        folderList = new ArrayList<>();
        adapter = new FolderAdapter(this, folderList, new FolderAdapter.OnFolderClickListener() {
            @Override
            public void onFolderClick(Folder folder) {
                if (folder != null && folder.getId() != null) {
                    startActivity(new Intent(FolderActivity.this, FolderDetailActivity.class)
                            .putExtra("FOLDER_ID", folder.getId())
                            .putExtra("FOLDER_NAME", folder.getName()));
                } else {
                    showToast("Dữ liệu thư mục không hợp lệ");
                }
            }

            @Override
            public void onRenameFolder(Folder folder, String newName) {
                if (folder != null && folder.getId() != null) {
                    renameFolderOnServer(folder, newName);
                } else {
                    showToast("Dữ liệu thư mục không hợp lệ");
                }
            }

            @Override
            public void onDeleteFolder(Folder folder) {
                if (folder != null && folder.getId() != null) {
                    new AlertDialog.Builder(FolderActivity.this)
                            .setTitle("Xóa thư mục")
                            .setMessage("Bạn có chắc muốn xóa thư mục " + folder.getName() + "?")
                            .setPositiveButton("Xóa", (dialog, which) -> deleteFolderFromServer(folder))
                            .setNegativeButton("Hủy", null)
                            .show();
                } else {
                    showToast("Dữ liệu thư mục không hợp lệ");
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch folders from API
        fetchFolders();
    }

    private void fetchFolders() {
        folderApiServices.getFoldersByUserId().enqueue(new Callback<ApiResponse<List<Folder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Folder>>> call, Response<ApiResponse<List<Folder>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMetadata() != null) {
                    List<Folder> fetchedFolders = response.body().getMetadata();
                    Log.d("API", "Fetched " + fetchedFolders.size() + " folders: " + fetchedFolders);
                    folderList.clear();
                    folderList.addAll(fetchedFolders);
                    adapter.notifyDataSetChanged();
                    if (folderList.isEmpty()) {
                        showToast("Không có thư mục nào");
                    }
                } else {
                    String errorMsg = "Lỗi khi tải thư mục";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = "Lỗi: " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("API", "Error parsing error body: " + e.getMessage());
                        }
                    }
                    showToast(errorMsg);
                    Log.e("API", "getFoldersByUserId Error: " + response.code() + ", URL: " + call.request().url());
                    folderList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Folder>>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
                Log.e("API", "getFoldersByUserId Failure: " + t.getMessage() + ", URL: " + call.request().url());
                folderList.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void renameFolderOnServer(Folder folder, String newName) {
        Log.d("API", "Attempting to rename folder: ID=" + folder.getId() + ", New Name=" + newName);
        FolderRequest request = new FolderRequest(newName, userId);
        folderApiServices.renameFolder(folder.getId(), request).enqueue(new Callback<ApiResponse<Folder>>() {
            @Override
            public void onResponse(Call<ApiResponse<Folder>> call, Response<ApiResponse<Folder>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMetadata() != null) {
                    Folder updatedFolder = response.body().getMetadata();
                    Log.d("API", "Folder renamed successfully: ID=" + updatedFolder.getId() + ", New Name=" + updatedFolder.getName());
                    showToast("Đã đổi tên thư mục thành: " + updatedFolder.getName());
                    // Update folder in local list immediately
                    for (int i = 0; i < folderList.size(); i++) {
                        if (folderList.get(i).getId().equals(updatedFolder.getId())) {
                            folderList.get(i).setName(updatedFolder.getName());
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    fetchFolders(); // Sync with backend
                } else {
                    String errorMsg = "Lỗi khi đổi tên thư mục";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = "Lỗi: " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("API", "Error parsing error body: " + e.getMessage());
                        }
                    }
                    showToast(errorMsg);
                    Log.e("API", "renameFolder Error: " + response.code() + ", URL: " + call.request().url());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Folder>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
                Log.e("API", "renameFolder Failure: " + t.getMessage() + ", URL: " + call.request().url());
            }
        });
    }

    private void deleteFolderFromServer(Folder folder) {
        Log.d("API", "Attempting to delete folder: ID=" + folder.getId() + ", Name=" + folder.getName());
        folderApiServices.deleteFolder(folder.getId()).enqueue(new Callback<ApiResponse<DeleteResult>>() {
            @Override
            public void onResponse(Call<ApiResponse<DeleteResult>> call, Response<ApiResponse<DeleteResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeleteResult result = response.body().getMetadata();
                    if (result != null && result.getDeletedCount() > 0) {
                        Log.d("API", "Folder deleted successfully: ID=" + folder.getId() + ", Deleted Count=" + result.getDeletedCount());
                        showToast("Đã xóa thư mục: " + folder.getName());
                    } else {
                        Log.w("API", "Folder not found or already deleted: ID=" + folder.getId());
                        showToast("Thư mục không tồn tại hoặc đã được xóa");
                    }
                    // Always remove from local list and sync with backend
                    folderList.remove(folder);
                    adapter.notifyDataSetChanged();
                    fetchFolders(); // Reset list to sync with backend
                } else {
                    String errorMsg = "Lỗi khi xóa thư mục";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = "Lỗi: " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("API", "Error parsing error body: " + e.getMessage());
                        }
                    }
                    showToast(errorMsg);
                    Log.e("API", "deleteFolder Error: " + response.code() + ", URL: " + call.request().url());
                    folderList.remove(folder); // Remove from UI on error
                    adapter.notifyDataSetChanged();
                    fetchFolders(); // Reset list
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DeleteResult>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
                Log.e("API", "deleteFolder Failure: " + t.getMessage() + ", URL: " + call.request().url());
                folderList.remove(folder); // Remove from UI on failure
                adapter.notifyDataSetChanged();
                fetchFolders(); // Reset list
            }
        });
    }

    private void setupButtonListeners() {
        FloatingActionButton addButton = findViewById(R.id.addFolderFab);
        addButton.setOnClickListener(v -> {
            Log.d("API", "Add folder button clicked");
            dialogHelper.showAddFolderDialog();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_folder) {
                fetchFolders();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_folder);
    }

    public void addFolder(Folder folder) {
        FolderRequest request = new FolderRequest(folder.getName(), userId);
        Log.d("API", "Sending create folder request: Name=" + folder.getName() + ", UserId=" + userId);
        folderApiServices.createFolder(request).enqueue(new Callback<ApiResponse<Folder>>() {
            @Override
            public void onResponse(Call<ApiResponse<Folder>> call, Response<ApiResponse<Folder>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMetadata() != null) {
                    Folder createdFolder = response.body().getMetadata();
                    Log.d("API", "Folder created successfully: ID=" + createdFolder.getId() + ", Name=" + createdFolder.getName());
                    fetchFolders(); // Refresh to include new folder
                    showToast("Đã thêm thư mục: " + createdFolder.getName());
                } else {
                    String errorMsg = "Lỗi khi tạo thư mục";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = "Lỗi: " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("API", "Error parsing error body: " + e.getMessage());
                        }
                    }
                    showToast(errorMsg);
                    Log.e("API", "createFolder Error: " + response.code() + ", URL: " + call.request().url());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Folder>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
                Log.e("API", "createFolder Failure: " + t.getMessage() + ", URL: " + call.request().url());
            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}