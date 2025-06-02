package com.example.final_project.adapters;
import com.example.final_project.requests.AddToFolderRequest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.activity.DocumentDetailActivity;
import com.example.final_project.bottom_sheet.EditDocumentBottomSheet;
import com.example.final_project.models.ApiResponse;
import com.example.final_project.models.Document;
import com.example.final_project.models.Folder;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.FolderApiServices;
import com.example.final_project.networks.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private static final String TAG = "DocumentAdapter";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final Context context;
    private final List<Document> documentList;
    private final List<Document> allDocuments;
    private boolean showingPinned = false;
    private final DocumentApiServices apiServices;
    private final FolderApiServices folderApiServices;

    public DocumentAdapter(Context context, List<Document> documentList) {
        this.context = context;
        this.documentList = documentList;
        this.allDocuments = new ArrayList<>(documentList);
        this.apiServices = RetrofitClient.getDocumentApiService(context);
        this.folderApiServices = RetrofitClient.getFolderApiService(context);
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documentList.get(position);
        String type;
        switch (document.getType()) {
            case "application/pdf":
                type = "PDF";
                break;
            case "application/msword":
                type = "DOC";
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                type = "DOCX";
                break;
            default:
                type = "Unknown";
                break;
        }
        holder.typeText.setText(type);
        holder.titleText.setText(document.getTitle());

        // Format the timestamp for display
        String timestamp = document.getUpdatedAt() != null ? document.getUpdatedAt() : document.getCreatedAt();
        if (timestamp != null) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                displayFormat.setTimeZone(TimeZone.getDefault());
                Date date = isoFormat.parse(timestamp);
                holder.dateText.setText(displayFormat.format(date));
            } catch (ParseException e) {
                holder.dateText.setText(timestamp);
            }
        } else {
            holder.dateText.setText("N/A");
        }

        holder.pinnedIcon.setVisibility(document.isPinned() ? View.VISIBLE : View.GONE);

        holder.moreButton.setOnClickListener(v -> showPopupMenu(v, position));
        holder.itemView.setOnClickListener(v -> openDocumentDetail(document));
    }

    private void showPopupMenu(View view, int position) {
        Document document = documentList.get(position);
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.file_options_menu);

        MenuItem pinItem = popup.getMenu().findItem(R.id.action_pin);
        pinItem.setTitle(document.isPinned() ? "Bỏ ghim" : "Ghim");

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_open) {
                openDocumentDetail(document);
                return true;
            } else if (id == R.id.action_edit) {
                EditDocumentBottomSheet bottomSheet = EditDocumentBottomSheet.newInstance(
                        document.getId(),
                        document.getTitle(),
                        new ArrayList<>(document.getTags())
                );
                bottomSheet.setOnDocumentUpdatedListener(updatedDoc -> {
                    documentList.set(position, updatedDoc);
                    notifyItemChanged(position);
                });
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                return true;
            } else if (id == R.id.action_share) {
                ShareBottomSheet bottomSheet = ShareBottomSheet.newInstance(document.getId(), document.getTitle());
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                return true;
            } else if (id == R.id.action_pin) {
                togglePinStatus(position);
                return true;
            } else if (id == R.id.action_delete) {
                showDeleteConfirmation(position);
                return true;
            } else if (id == R.id.action_add_to_folder) {
                validateAndAddToFolder(document.getId());
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void validateAndAddToFolder(String documentId) {
        AlertDialog loadingDialog = new AlertDialog.Builder(context)
                .setMessage("Đang kiểm tra tài liệu...")
                .setCancelable(false)
                .create();
        loadingDialog.show();

        Call<Document> validateDocCall = apiServices.getDocumentById(documentId);
        validateDocCall.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call, Response<Document> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showFolderSelectionDialog(documentId, loadingDialog);
                } else {
                    loadingDialog.dismiss();
                    String errorMsg = response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing validateDoc errorBody", e);
                        }
                    }
                    Log.e(TAG, "Validate document error: " + errorMsg);
                    Toast.makeText(context, "Tài liệu không tồn tại: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {
                loadingDialog.dismiss();
                Log.e(TAG, "Validate document failed: " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi kiểm tra tài liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFolderSelectionDialog(String documentId, AlertDialog loadingDialog) {
        loadingDialog.setMessage("Đang tải danh sách thư mục...");
        Call<ApiResponse<List<Folder>>> call = folderApiServices.getFoldersByUserId();
        call.enqueue(new Callback<ApiResponse<List<Folder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Folder>>> call, Response<ApiResponse<List<Folder>>> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    List<Folder> folders = response.body().getData();
                    if (folders != null && !folders.isEmpty()) {
                        String[] folderNames = folders.stream().map(Folder::getName).toArray(String[]::new);
                        String[] folderIds = folders.stream().map(Folder::getId).toArray(String[]::new);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Chọn thư mục");
                        builder.setItems(folderNames, (dialog, which) -> {
                            String selectedFolderId = folderIds[which];
                            addDocumentToFolder(documentId, selectedFolderId);
                        });
                        builder.setNegativeButton("Hủy", null);
                        builder.show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("Không có thư mục")
                                .setMessage("Bạn chưa có thư mục nào. Tạo thư mục mới?")
                                .setPositiveButton("Tạo", (dialog, which) -> {
                                    Toast.makeText(context, "Chức năng tạo thư mục chưa được triển khai", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Lỗi khi tải danh sách thư mục: " + response.message();
                    Log.e(TAG, "Error fetching folders: " + errorMsg);
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Folder>>> call, Throwable t) {
                loadingDialog.dismiss();
                Log.e(TAG, "Failed to fetch folders: " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDocumentToFolder(String documentId, String folderId) {
        if (folderId == null || folderId.isEmpty()) {
            Toast.makeText(context, "ID thư mục không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        AddToFolderRequest request = new AddToFolderRequest(folderId);
        String json = gson.toJson(request);
        Log.d(TAG, "Adding document " + documentId + " to folder " + folderId + " with request: " + json);
        RequestBody requestBody = RequestBody.create(JSON, json);

        AlertDialog loadingDialog = new AlertDialog.Builder(context)
                .setMessage("Đang thêm tài liệu vào thư mục...")
                .setCancelable(false)
                .create();
        loadingDialog.show();

        Call<Document> call = apiServices.addDocumentToFolder(documentId, requestBody);
        call.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call, Response<Document> response) {
                loadingDialog.dismiss();
                Log.d(TAG, "addDocumentToFolder response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Đã thêm tài liệu vào thư mục", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = response.message();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(errorBody, ApiResponse.class);
                            errorMsg = errorResponse.getMessage() != null ? errorResponse.getMessage() : errorBody;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing errorBody", e);
                    }
                    Log.e(TAG, "Add to folder error: " + errorMsg);
                    if (errorMsg.contains("Error moving document")) {
                        Toast.makeText(context, "Lỗi: Không thể di chuyển tài liệu. Vui lòng kiểm tra lại thư mục.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {
                loadingDialog.dismiss();
                Log.e(TAG, "addDocumentToFolder failed for document " + documentId + ": " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteConfirmation(int position) {
        Document document = documentList.get(position);
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài liệu '" + document.getTitle() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteDocument(document.getId());
                    documentList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Đã xóa tài liệu: " + document.getTitle(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Đã hủy thao tác")
                            .setMessage("Bạn đã hủy thao tác xóa.")
                            .setPositiveButton("OK", null)
                            .show();
                })
                .show();
    }

    private void openDocumentDetail(Document document) {
        Intent intent = DocumentDetailActivity.newIntent(
                context, document.getTitle(), document.getType(), document.getUrl(), document.getId());
        context.startActivity(intent);
    }

    private void togglePinStatus(int position) {
        Document document = documentList.get(position);
        boolean newPinnedStatus = !document.isPinned();
        document.setPinned(newPinnedStatus);

        for (Document doc : allDocuments) {
            if (doc.getTitle().equals(document.getTitle()) &&
                    doc.getType().equals(document.getType())) {
                doc.setPinned(newPinnedStatus);
                break;
            }
        }
        Call<Document> call = apiServices.togglePin(document.getId());
        call.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call, Response<Document> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context,
                            newPinnedStatus ? "Đã ghim: " + document.getTitle() : "Đã bỏ ghim: " + document.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                } else {
                    // Revert the pin status if the API call fails
                    document.setPinned(!newPinnedStatus);
                    for (Document doc : allDocuments) {
                        if (doc.getTitle().equals(document.getTitle()) &&
                                doc.getType().equals(document.getType())) {
                            doc.setPinned(!newPinnedStatus);
                            break;
                        }
                    }
                    String errorMsg = response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing togglePin errorBody", e);
                        }
                    }
                    Log.e(TAG, "Toggle pin error: " + errorMsg);
                    Toast.makeText(context, "Lỗi khi cập nhật trạng thái ghim: " + errorMsg, Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {
                // Revert the pin status if the API call fails
                document.setPinned(!newPinnedStatus);
                for (Document doc : allDocuments) {
                    if (doc.getTitle().equals(document.getTitle()) &&
                            doc.getType().equals(document.getType())) {
                        doc.setPinned(!newPinnedStatus);
                        break;
                    }
                }
                Log.e(TAG, "Toggle pin failed: " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
            }
        });
    }

    private void togglePin(String id) {
        Call<Document> call = apiServices.togglePin(id);
        call.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call, Response<Document> response) {
                if(response.isSuccessful())
                {

                }
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDocument(String id) {
        Call<ResponseBody> call = apiServices.deleteDocument(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã xóa tài liệu", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing deleteDocument errorBody", e);
                        }
                    }
                    Log.e(TAG, "Delete document error: " + errorMsg);
                    Toast.makeText(context, "Lỗi khi xóa tài liệu: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Delete document failed: " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showPinnedDocuments() {
        showingPinned = true;
        List<Document> pinnedDocs = new ArrayList<>();
        for (Document doc : allDocuments) {
            if (doc.isPinned()) {
                pinnedDocs.add(doc);
            }
        }
        documentList.clear();
        documentList.addAll(pinnedDocs);
        notifyDataSetChanged();
    }

    public boolean isShowingPinned() {
        return showingPinned;
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, titleText, dateText;
        ImageButton moreButton;
        ImageView pinnedIcon;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.documentTypeText);
            titleText = itemView.findViewById(R.id.documentTitleText);
            dateText = itemView.findViewById(R.id.documentDateText);
            moreButton = itemView.findViewById(R.id.moreButton);
            pinnedIcon = itemView.findViewById(R.id.pinnedIcon);
        }
    }

    public static class ShareBottomSheet extends BottomSheetDialogFragment {

        private String documentId;
        private String title;

        public static ShareBottomSheet newInstance(String documentId, String title) {
            ShareBottomSheet fragment = new ShareBottomSheet();
            Bundle args = new Bundle();
            args.putString("documentId", documentId);
            args.putString("title", title);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater,
                @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.bottom_sheet_share, container, false);

            if (getArguments() != null) {
                documentId = getArguments().getString("documentId");
                title = getArguments().getString("title");
            }

            TextView shareTitle = view.findViewById(R.id.shareTitle);
            TextView shareSubtitle = view.findViewById(R.id.shareSubtitle);
            Button btnCreateLink = view.findViewById(R.id.btnCreateLink);
            Button btnCancel = view.findViewById(R.id.btnCancel);

            shareTitle.setText("Chia sẻ tài liệu");
            shareSubtitle.setText(title);

            btnCreateLink.setOnClickListener(v -> {
                String shareUrl = "https://example.com/document/" + documentId;
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Share Link", shareUrl);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Liên kết đã được sao chép", Toast.LENGTH_SHORT).show();
                dismiss();
            });

            btnCancel.setOnClickListener(v -> dismiss());

            return view;
        }
    }

}