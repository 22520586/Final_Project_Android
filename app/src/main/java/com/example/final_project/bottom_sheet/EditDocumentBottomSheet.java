package com.example.final_project.bottom_sheet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.final_project.R;
import com.example.final_project.models.Document;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.RetrofitClient;
import com.example.final_project.requests.UpdateRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDocumentBottomSheet extends BottomSheetDialogFragment {
    private Context context;
    private String title;
    private String description;
    private ArrayList<String> selectedTags = new ArrayList<>();

    public interface OnDocumentUpdatedListener {
        void onDocumentUpdated(Document updatedDoc);
    }

    private OnDocumentUpdatedListener listener;

    public void setOnDocumentUpdatedListener(OnDocumentUpdatedListener listener) {
        this.listener = listener;
    }
    public static EditDocumentBottomSheet newInstance(String id,String title, ArrayList<String> tags) {
        EditDocumentBottomSheet fragment = new EditDocumentBottomSheet();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("title", title);
        args.putStringArrayList("tags", tags);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_edit_document, container, false);

        String documentId = getArguments().getString("id");

        EditText titleEdit = view.findViewById(R.id.editTitle);
        EditText descEdit = view.findViewById(R.id.editDescription);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        ImageButton btnAddTag = view.findViewById(R.id.btnAddTag); // Dấu "+"
        LinearLayout tagContainer = view.findViewById(R.id.tagContainer); // Container để hiển thị tag
        DocumentApiServices apiServices = RetrofitClient.getDocumentApiService(requireContext());
        if (getArguments() != null) {
            title = getArguments().getString("title");
            description = getArguments().getString("description");
            titleEdit.setText(title);
            selectedTags = getArguments().getStringArrayList("tags");
            descEdit.setText(description);
        }

        // Hiển thị danh sách tag đã chọn (nếu có)
        displayTags(selectedTags, tagContainer);

        // Xử lý sự kiện nhấn dấu "+"
        btnAddTag.setOnClickListener(v -> {
            EditTagBottomSheet editTagBottomSheet = EditTagBottomSheet.newInstance(new ArrayList<String>(selectedTags));
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
            String newTags = String.join(",", selectedTags);
            UpdateRequest updateDoc = new UpdateRequest(documentId, updatedTitle, newTags);

            Call<ResponseBody> call = apiServices.updateDocument(documentId, updateDoc);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                    {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        }
                        if(listener != null)
                        {
                            Document updatedDoc = new Document(documentId, updatedTitle, selectedTags);
                            listener.onDocumentUpdated(updatedDoc);
                        }
                    }
                    else {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(isAdded()) {
                        Log.d("Lỗi kết nối 2: ", t.getMessage());
                        Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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