package com.example.final_project.helpers;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.final_project.activity.FolderActivity;
import com.example.final_project.R;
import com.example.final_project.models.Folder;

public class AddFolderDialogHelper {

    private final Context context;
    private final FolderActivity activity;
    private Dialog dialog;

    public AddFolderDialogHelper(FolderActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public void showAddFolderDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_folder);

        EditText folderNameInput = dialog.findViewById(R.id.inputFolderName);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Kiểm tra null để tránh crash
        if (folderNameInput == null || btnSave == null || btnCancel == null) {
            activity.showToast("Lỗi: Không thể hiển thị dialog thêm thư mục!");
            return;
        }

        btnSave.setOnClickListener(v -> {
            String folderName = folderNameInput.getText().toString().trim();
            if (!TextUtils.isEmpty(folderName)) {
                activity.addFolder(new Folder(folderName));
                activity.showToast("Thêm thư mục thành công!");
                dialog.dismiss();
            } else {
                activity.showToast("Vui lòng nhập tên thư mục!");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}