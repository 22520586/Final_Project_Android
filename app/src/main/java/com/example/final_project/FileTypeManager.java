package com.example.final_project;

public class FileTypeManager {
    private MainActivity activity;

    public FileTypeManager(MainActivity activity) {
        this.activity = activity;
    }

    public void handleFileTypeSelection(String fileType) {
        // Implement logic for file type selection
        activity.showToast("Đã chọn loại tập tin: " + fileType);
        // Bạn có thể sử dụng thông tin này để lọc tài liệu hoặc tạo tài liệu mới

        // Đây là nơi để thêm logic lọc theo loại file
        // Ví dụ: filterDocumentsByType(fileType);
    }

    public void filterDocumentsByType(String fileType) {
        // TODO: Implement filtering by file type
        // Cập nhật RecyclerView với dữ liệu đã lọc
    }
}