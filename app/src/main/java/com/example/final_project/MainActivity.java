package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.final_project.utils.FileUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

    private DocumentApiServices apiServices = RetrofitClient.getDocumentApiService(this);

    private DocumentManager documentManager;
    private AddDocumentDialogHelper dialogHelper;
    private DropdownMenuHelper dropdownMenuHelper;
    private UserProfileDialogHelper userProfileDialogHelper;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "SmartToken";
    private static final String KEY_TOKEN = "token";
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        searchEditText = findViewById(R.id.searchEditText);
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

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && dialogHelper != null) {
            dialogHelper.handlePermissionResult(requestCode, permissions, grantResults);
        }
    }


        private void uploadFile(Uri fileUri, String title, String tags) {
            File file = FileUtils.getFileFromUri(this, fileUri); // Xem bên dưới

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(fileUri)), file);
            MultipartBody.Part documentPart = MultipartBody.Part.createFormData("document", file.getName(), requestFile);

            RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody tagsPart = RequestBody.create(MediaType.parse("text/plain"), tags);

            Call<ResponseBody> call = apiServices.uploadDocument(documentPart, titlePart, tagsPart);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Tải lên thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    public String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            showUploadMetadataDialog(fileUri);
        }
    }

    private void fetchDocuments()
    {
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
                getPinnedDocuments();
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

    }

    private void getPinnedDocuments()
    {
        Call<List<Document>> call = apiServices.getPinnedDocuments();
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

    private void performSearch(String query) {
        Call<List<Document>> call = apiServices.searchDocuments(query);
        call.enqueue(new Callback<List<Document>>() {
            @Override
            public void onResponse(Call<List<Document>> call, Response<List<Document>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    documentList.clear();
                    documentList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    showToast("Lỗi khi tìm kiếm tài liệu: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Document>> call, Throwable t) {
                showToast("Lỗi: " + t.getMessage());
            }
        });

    }

    private void showUploadMetadataDialog(Uri fileUri)
    {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.activity_input_document_data, null);

        EditText editFileName = dialogView.findViewById(R.id.editFileName);
        EditText editTags = dialogView.findViewById(R.id.editTags);

        // Tạo dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nhập thông tin tài liệu")
                .setView(dialogView)
                .setPositiveButton("Upload", (dialogInterface, i) -> {
                    String title = editFileName.getText().toString().trim();
                    String tags = editTags.getText().toString().trim();
                    if (!title.isEmpty()) {
                        uploadFile(fileUri, title, tags);
                    } else {
                        Toast.makeText(this, "Tên tài liệu không được để trống", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .create();

        dialog.show();
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

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
    }
}