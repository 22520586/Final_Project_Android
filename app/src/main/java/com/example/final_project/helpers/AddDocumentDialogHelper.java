package com.example.final_project.helpers;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.final_project.MainActivity;
import com.example.final_project.R;

public class AddDocumentDialogHelper {
    private MainActivity activity;

    public AddDocumentDialogHelper(MainActivity activity) {
        this.activity = activity;
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
        uploadDocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showToast("Tải lên từ thiết bị");
                // Thực hiện chức năng tải lên tài liệu
                // TODO: Implement file picker
            }
        });

        importCloudLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showToast("Nhập từ đám mây");
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
                activity.showToast("Tiếp tục");
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