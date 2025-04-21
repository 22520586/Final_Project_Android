package com.example.final_project;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<Document> documentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.documentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo dữ liệu ban đầu
        documentList = new ArrayList<>();
        documentList.add(new Document("PDF", "Báo cáo tài chính Q2"));
        documentList.add(new Document("DOC", "Đề xuất chính sách mới"));
        documentList.add(new Document("TXT", "Kết luận cuộc họp"));
        documentList.add(new Document("XLS", "Dự toán ngân sách"));

        // Thiết lập adapter
        adapter = new DocumentAdapter(this, documentList);
        recyclerView.setAdapter(adapter);

        // Xử lý sự kiện nút thêm
        FloatingActionButton addButton = findViewById(R.id.addDocumentFab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDocumentDialog();
            }
        });
    }

    private void showAddDocumentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_document);
        dialog.setCancelable(true);

        // Get dialog elements
        LinearLayout uploadDocLayout = dialog.findViewById(R.id.uploadDocumentLayout);
        LinearLayout importCloudLayout = dialog.findViewById(R.id.importCloudLayout);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button continueButton = dialog.findViewById(R.id.continueButton);

        // Set click listeners
        uploadDocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Tải lên từ thiết bị", Toast.LENGTH_SHORT).show();
                // Thực hiện chức năng tải lên tài liệu
                // TODO: Implement file picker
            }
        });

        importCloudLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Nhập từ đám mây", Toast.LENGTH_SHORT).show();
                // Thực hiện chức năng nhập từ đám mây
                // TODO: Implement cloud storage integration
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Tiếp tục", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // TODO: Implement next step based on selection
            }
        });

        dialog.show();
        // Make dialog width match parent
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }
}