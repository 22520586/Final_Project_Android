package com.example.final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private Context context;
    private List<Document> documentList;
    private List<Document> allDocuments;  // Lưu danh sách đầy đủ
    private boolean showingPinned = false;

    public DocumentAdapter(Context context, List<Document> documentList) {
        this.context = context;
        this.documentList = documentList;
        this.allDocuments = new ArrayList<>(documentList);  // Tạo bản sao
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
        holder.typeText.setText(document.getType());
        holder.titleText.setText(document.getTitle());
        holder.dateText.setText(document.getDate());

        // Hiển thị/ẩn icon ghim dựa trên trạng thái của tài liệu
        if (document.isPinned()) {
            holder.pinnedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.pinnedIcon.setVisibility(View.GONE);
        }

        holder.moreButton.setOnClickListener(v -> {
            showPopupMenu(v, position);
        });

        holder.itemView.setOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn vào item
            Toast.makeText(context, "Đã chọn: " + document.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showPopupMenu(View view, int position) {
        Document document = documentList.get(position);
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.file_options_menu);

        // Đổi text của menu item "Ghim" thành "Bỏ ghim" nếu tài liệu đã được ghim
        MenuItem pinItem = popup.getMenu().findItem(R.id.action_pin);
        if (document.isPinned()) {
            pinItem.setTitle("Bỏ ghim");
        } else {
            pinItem.setTitle("Ghim");
        }

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_open) {
                Toast.makeText(context, "Mở: " + document.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_edit) {
                Toast.makeText(context, "Chỉnh sửa: " + document.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_share) {
                Toast.makeText(context, "Chia sẻ: " + document.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_pin) {
                togglePinStatus(position);
                return true;
            } else if (id == R.id.action_delete) {
                deleteDocument(position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void togglePinStatus(int position) {
        Document document = documentList.get(position);
        // Đảo trạng thái ghim
        document.setPinned(!document.isPinned());

        // Cập nhật trạng thái trong danh sách đầy đủ
        for (Document doc : allDocuments) {
            if (doc.getTitle().equals(document.getTitle()) &&
                    doc.getType().equals(document.getType())) {
                doc.setPinned(document.isPinned());
                break;
            }
        }

        // Thông báo khi ghim/bỏ ghim
        if (document.isPinned()) {
            Toast.makeText(context, "Đã ghim: " + document.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Đã bỏ ghim: " + document.getTitle(), Toast.LENGTH_SHORT).show();
        }

        notifyItemChanged(position);
    }

    private void deleteDocument(int position) {
        Document document = documentList.get(position);
        // Xóa khỏi danh sách hiện tại
        documentList.remove(position);

        // Xóa khỏi danh sách đầy đủ
        for (int i = 0; i < allDocuments.size(); i++) {
            Document doc = allDocuments.get(i);
            if (doc.getTitle().equals(document.getTitle()) &&
                    doc.getType().equals(document.getType())) {
                allDocuments.remove(i);
                break;
            }
        }

        Toast.makeText(context, "Đã xóa: " + document.getTitle(), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    // Phương thức để hiển thị tất cả tài liệu
    public void showAllDocuments() {
        showingPinned = false;
        documentList.clear();
        documentList.addAll(allDocuments);
        notifyDataSetChanged();
    }

    // Phương thức để hiển thị chỉ những tài liệu đã ghim
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

    // Phương thức để kiểm tra đang hiển thị loại tài liệu nào
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
        ImageView pinnedIcon;  // Thêm ImageView cho icon ghim

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.documentTypeText);
            titleText = itemView.findViewById(R.id.documentTitleText);
            dateText = itemView.findViewById(R.id.documentDateText);
            moreButton = itemView.findViewById(R.id.moreButton);
            pinnedIcon = itemView.findViewById(R.id.pinnedIcon);  // Ánh xạ ImageView từ layout
        }
    }
}