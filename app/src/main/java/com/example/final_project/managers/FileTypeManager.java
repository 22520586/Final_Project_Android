package com.example.final_project.managers;

import android.content.Context;
import android.widget.Toast;

import com.example.final_project.MainActivity;
import com.example.final_project.models.Document;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileTypeManager {
    private MainActivity activity;
    private Context context;
    private DocumentApiServices apiServices;

    public FileTypeManager(MainActivity activity) {

        this.activity = activity;
        this.context = activity;
        this.apiServices = RetrofitClient.getDocumentApiService(context);
    }

    public void handleFileTypeSelection(String fileType) {
        // Implement logic for file type selection
        activity.showToast("Đã chọn loại tập tin: " + fileType.toUpperCase());
        // Bạn có thể sử dụng thông tin này để lọc tài liệu hoặc tạo tài liệu mới
        String type;
        switch (fileType) {
            case "pdf":
                type = "application/pdf";
                break;
            case "docx":
                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                break;
            case "txt":
                type = "text/plain";
                break;
            default:
                type = "unknown";
                break;
        }
        // Đây là nơi để thêm logic lọc theo loại file
        filterDocumentsByType(type, "");
    }

    public void filterDocumentsByType(String fileType, String tags) {
        // TODO: Implement filtering by file type
        // Cập nhật RecyclerView với dữ liệu đã lọc
        Call<List<Document>> call = apiServices.filterDocument(tags, fileType);
        call.enqueue(new Callback<List<Document>>() {
            @Override
            public void onResponse(Call<List<Document>> call, Response<List<Document>> response) {
                if (response.isSuccessful()) {
                    List<Document> filteredDocuments = response.body();
                    activity.updateFilteredDocuments(filteredDocuments);
                }
            }

            @Override
            public void onFailure(Call<List<Document>> call, Throwable t) {
                Toast.makeText(context, "Lỗi khi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}