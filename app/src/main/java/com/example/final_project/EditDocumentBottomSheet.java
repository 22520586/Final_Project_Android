package com.example.final_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class EditDocumentBottomSheet extends BottomSheetDialogFragment {

    private String title;
    private String description;
    private List<String> selectedTags = new ArrayList<>();

    public static EditDocumentBottomSheet newInstance(String title, String description) {
        EditDocumentBottomSheet fragment = new EditDocumentBottomSheet();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_edit_document, container, false);

        EditText titleEdit = view.findViewById(R.id.editTitle);
        EditText descEdit = view.findViewById(R.id.editDescription);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        ImageButton btnAddTag = view.findViewById(R.id.btnAddTag); // Dấu "+"
        LinearLayout tagContainer = view.findViewById(R.id.tagContainer); // Container để hiển thị tag

        if (getArguments() != null) {
            title = getArguments().getString("title");
            description = getArguments().getString("description");
            titleEdit.setText(title);
            descEdit.setText(description);
        }

        // Hiển thị danh sách tag đã chọn (nếu có)
        displayTags(selectedTags, tagContainer);

        // Xử lý sự kiện nhấn dấu "+"
        btnAddTag.setOnClickListener(v -> {
            EditTagBottomSheet editTagBottomSheet = EditTagBottomSheet.newInstance();
            editTagBottomSheet.setOnTagsSelectedListener(tags -> {
                // Nhận danh sách tag từ EditTagBottomSheet
                selectedTags.clear();
                selectedTags.addAll(tags);
                // Hiển thị lại danh sách tag
                displayTags(selectedTags, tagContainer);
            });
            editTagBottomSheet.show(getParentFragmentManager(), editTagBottomSheet.getTag());
        });

        btnSave.setOnClickListener(v -> {
            String updatedTitle = titleEdit.getText().toString();
            String updatedDesc = descEdit.getText().toString();

            // TODO: Gửi dữ liệu mới (bao gồm selectedTags) về adapter hoặc Activity nếu cần
            // Ví dụ: Bạn có thể gửi updatedTitle, updatedDesc, và selectedTags về Activity

            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private void displayTags(List<String> tags, LinearLayout container) {
        container.removeAllViews();
        for (String tag : tags) {
            TextView tagView = new TextView(getContext());
            tagView.setText(tag);
            tagView.setBackgroundResource(R.drawable.bg_tag); // Đảm bảo bạn đã định nghĩa drawable này
            tagView.setTextColor(getResources().getColor(android.R.color.black)); // Màu chữ
            tagView.setPadding(12, 6, 12, 6); // Padding cho tag
            tagView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            ((LinearLayout.LayoutParams) tagView.getLayoutParams()).setMargins(0, 0, 12, 0); // Khoảng cách giữa các tag

            container.addView(tagView);
        }
    }
}