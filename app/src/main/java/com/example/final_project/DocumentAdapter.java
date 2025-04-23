package com.example.final_project;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


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

        if (document.isPinned()) {
            holder.pinnedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.pinnedIcon.setVisibility(View.GONE);
        }

        holder.moreButton.setOnClickListener(v -> {
            showPopupMenu(v, position);
        });

        holder.itemView.setOnClickListener(v -> {
            openDocumentDetail(document);
        });
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
                        document.getTitle(),
                        document.getDescription() // bạn có thể sửa nếu không có mô tả
                );
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                return true;

        } else if (id == R.id.action_share) {
                ShareBottomSheet bottomSheet = ShareBottomSheet.newInstance(document.getTitle(), document.getType());
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());


            } else if (id == R.id.action_pin) {
                togglePinStatus(position);
                return true;
            } else if (id == R.id.action_delete) {
                showDeleteConfirmation(position);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void shareDocument(Document document) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        String shareContent = "Tiêu đề: " + document.getTitle() + "\nLoại: " + document.getType();
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ tài liệu");

        // Tạo danh sách app chia sẻ (loại trừ Bluetooth)
        Intent chooser = Intent.createChooser(sendIntent, "Chia sẻ tài liệu");

        // Optional: chặn app Bluetooth nếu muốn lọc
        List<Intent> targetIntents = new ArrayList<>();
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(sendIntent, 0);
        for (ResolveInfo resInfo : resInfoList) {
            String packageName = resInfo.activityInfo.packageName;
            if (!packageName.toLowerCase().contains("bluetooth")) {
                Intent targetedIntent = new Intent(Intent.ACTION_SEND);
                targetedIntent.setType("text/plain");
                targetedIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                targetedIntent.setPackage(packageName);
                targetIntents.add(targetedIntent);
            }
        }

        if (!targetIntents.isEmpty()) {
            Intent finalChooser = Intent.createChooser(targetIntents.remove(0), "Chia sẻ tài liệu");
            finalChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[0]));
            context.startActivity(finalChooser);
        } else {
            Toast.makeText(context, "Không tìm thấy ứng dụng chia sẻ phù hợp.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDeleteConfirmation(int position) {
        Document document = documentList.get(position);

        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài liệu '" + document.getTitle() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteDocument(position);
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
                context, document.getTitle(), document.getType());
        context.startActivity(intent);
    }

    private void togglePinStatus(int position) {
        Document document = documentList.get(position);
        document.setPinned(!document.isPinned());

        for (Document doc : allDocuments) {
            if (doc.getTitle().equals(document.getTitle()) &&
                    doc.getType().equals(document.getType())) {
                doc.setPinned(document.isPinned());
                break;
            }
        }

        Toast.makeText(context,
                document.isPinned() ? "Đã ghim: " + document.getTitle()
                        : "Đã bỏ ghim: " + document.getTitle(),
                Toast.LENGTH_SHORT).show();

        notifyItemChanged(position);
    }

    private void deleteDocument(int position) {
        Document document = documentList.get(position);
        documentList.remove(position);

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

    public void showAllDocuments() {
        showingPinned = false;
        documentList.clear();
        documentList.addAll(allDocuments);
        notifyDataSetChanged();
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

        private String title;
        private String type;

        public static ShareBottomSheet newInstance(String title, String type) {
            ShareBottomSheet fragment = new ShareBottomSheet();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("type", type);
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
                title = getArguments().getString("title");
                type = getArguments().getString("type");
            }

            TextView shareTitle = view.findViewById(R.id.shareTitle);
            TextView shareSubtitle = view.findViewById(R.id.shareSubtitle);

            shareTitle.setText("Chia sẻ tài liệu");
            shareSubtitle.setText(title + " (" + type + ")");

            view.findViewById(R.id.btnEmail).setOnClickListener(v -> shareVia("email"));
            view.findViewById(R.id.btnSms).setOnClickListener(v -> shareVia("sms"));
            view.findViewById(R.id.btnDrive).setOnClickListener(v -> shareVia("drive"));
            view.findViewById(R.id.btnLink).setOnClickListener(v -> shareVia("link"));
            view.findViewById(R.id.btnMore).setOnClickListener(v -> shareVia("more"));

            return view;
        }

        private void shareVia(String method) {
            Toast.makeText(getContext(), "Chia sẻ qua: " + method, Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
