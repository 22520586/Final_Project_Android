package com.example.final_project.bottom_sheet;

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

import com.example.final_project.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class EditTagBottomSheet extends BottomSheetDialogFragment {

    private List<String> currentTags = new ArrayList<>();
    private List<String> commonTags = Arrays.asList("Báo cáo", "Dự án", "Quan trọng", "Cá nhân", "Tài chính");
    private OnTagsSelectedListener tagsSelectedListener;

    // Interface để gửi danh sách tag đã chọn về
    public interface OnTagsSelectedListener {
        void onTagsSelected(List<String> tags);
    }

    public static EditTagBottomSheet newInstance(ArrayList<String> existingTags) {
        EditTagBottomSheet fragment = new EditTagBottomSheet();
        Bundle args = new Bundle();
        args.putStringArrayList("existingTags", existingTags);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnTagsSelectedListener(OnTagsSelectedListener listener) {
        this.tagsSelectedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_tag, container, false);

        // Tham chiếu các view
        LinearLayout currentTagsContainer = view.findViewById(R.id.currentTagsContainer);
        LinearLayout commonTagsContainer = view.findViewById(R.id.commonTagsContainer);
        EditText inputNewTag = view.findViewById(R.id.inputNewTag);
        ImageButton btnAddTag = view.findViewById(R.id.btnAddTag);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // Hiển thị các tag phổ biến
        displayTags(commonTags, commonTagsContainer, tag -> {
            if (!currentTags.contains(tag)) {
                currentTags.add(tag);
                displayCurrentTags(currentTagsContainer);
            }
        });

        if (getArguments() != null) {
            ArrayList<String> existingTags = getArguments().getStringArrayList("existingTags");
            if (existingTags != null) {
                currentTags.addAll(existingTags);
            }
            displayCurrentTags(currentTagsContainer);
        }

        // Xử lý thêm tag mới
        btnAddTag.setOnClickListener(v -> {
            String newTag = inputNewTag.getText().toString().trim();
            if (!TextUtils.isEmpty(newTag)) {
                if (!currentTags.contains(newTag)) {
                    currentTags.add(newTag);
                    displayCurrentTags(currentTagsContainer);
                    inputNewTag.setText("");
                }
            }
        });

        // Xử lý nút xác nhận
        btnConfirm.setOnClickListener(v -> {
            if (tagsSelectedListener != null) {
                tagsSelectedListener.onTagsSelected(currentTags);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private void displayCurrentTags(LinearLayout container) {
        container.removeAllViews();
        for (String tag : currentTags) {
            TextView tagView = new TextView(getContext());
            tagView.setText(tag);
            tagView.setBackgroundResource(R.drawable.bg_tag);
            tagView.setPadding(8, 4, 8, 4);
            tagView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            ((LinearLayout.LayoutParams) tagView.getLayoutParams()).setMargins(0, 0, 8, 0);

            container.addView(tagView);
        }
    }

    private void displayTags(List<String> tags, LinearLayout container, Consumer<String> onClick) {
        container.removeAllViews();
        for (String tag : tags) {
            TextView tagView = new TextView(getContext());
            tagView.setText(tag);
            tagView.setBackgroundResource(R.drawable.bg_common_tag);
            tagView.setPadding(8, 4, 8, 4);
            tagView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            ((LinearLayout.LayoutParams) tagView.getLayoutParams()).setMargins(0, 0, 8, 0);

            tagView.setOnClickListener(v -> onClick.accept(tag));
            container.addView(tagView);
        }
    }
}