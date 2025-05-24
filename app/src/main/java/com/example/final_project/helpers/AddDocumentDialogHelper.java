package com.example.final_project.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.activity.DocumentDetailActivity;
import com.example.final_project.models.Document;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class AddDocumentDialogHelper {
    private Activity activity;
    private Uri selectedFileUri;
    private ActivityResultLauncher<String> filePicker;
    private static final int STORAGE_PERMISSION_CODE = 100;



    public interface UploadDocumentCallback
    {
        void onUploadSuccess(Document document);
        void onUploadFailure();
    }
    public AddDocumentDialogHelper(Activity activity) {
        this.activity = activity;
    }

    public AddDocumentDialogHelper(Activity activity, UploadDocumentCallback callback)
    {
        this.activity = activity;

    }

    private void initializeFilePicker() {
        filePicker = ((MainActivity) activity).registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedFileUri = uri;
                        // Handle the selected file URI
                        Toast.makeText(activity, "Đã chọn file: " + getFileNameFromUri(uri), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(activity, "Không có file nào được chọn", Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }

    private String getFileNameFromUri(Uri uri)
    {
        String fileName = null;
        String[] projection = {android.provider.MediaStore.MediaColumns.DISPLAY_NAME};
        try (var cursor = activity.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.MediaStore.MediaColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
            }
        } catch (Exception e) {
            Log.e("AddDocumentDialogHelper", "Error getting file name: " + e.getMessage());
        }
        return fileName != null ? fileName : "unnamed_file";
    }
    public void showAddDocumentDialog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_document);
        dialog.setCancelable(true);

        // Get dialog elements
        LinearLayout uploadDocLayout = dialog.findViewById(R.id.uploadDocumentLayout);
        LinearLayout importCloudLayout = dialog.findViewById(R.id.importCloudLayout);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button continueButton = dialog.findViewById(R.id.continueButton);

        // Set click listeners
        uploadDocLayout.setOnClickListener(v -> {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                ((Activity) activity).startActivityForResult(intent, 101);

        });

        importCloudLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Nhập từ đám mây", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(activity, "Tiếp tục", Toast.LENGTH_SHORT).show();

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

    private void checkStoragePermissionAndPickFile() {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
        else {
            pickFile();
        }
    }

    public void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Người dùng đã từ chối vĩnh viễn, hướng dẫn mở cài đặt
                    Toast.makeText(activity, "Quyền truy cập bộ nhớ bị từ chối vĩnh viễn. Vui lòng cấp quyền trong cài đặt!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(android.net.Uri.fromParts("package", activity.getPackageName(), null));
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "Quyền truy cập bộ nhớ bị từ chối! Vui lòng cấp quyền để tiếp tục.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void pickFile()
    {
        filePicker.launch("*/*");
    }


}
