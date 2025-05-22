package com.example.final_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.models.Folder;
import com.example.final_project.R;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private Context context;
    private List<Folder> folderList;
    private OnFolderClickListener listener;

    public interface OnFolderClickListener {
        void onFolderClick(Folder folder);
        void onRenameFolder(Folder folder, String newName);
        void onDeleteFolder(Folder folder);
    }

    public FolderAdapter(Context context, List<Folder> folderList, OnFolderClickListener listener) {
        this.context = context;
        this.folderList = folderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        holder.folderName.setText(folder.getName());

        holder.itemView.setOnClickListener(v -> listener.onFolderClick(folder));

        holder.moreButton.setOnClickListener(v -> showPopupMenu(holder.moreButton, folder));
    }

    private void showPopupMenu(ImageButton moreButton, Folder folder) {
        PopupMenu popupMenu = new PopupMenu(context, moreButton);
        popupMenu.inflate(R.menu.folder_option_menu); // Sử dụng file folder_option_menu.xml

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_rename) {
                showRenameDialog(folder);
                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                listener.onDeleteFolder(folder);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showRenameDialog(Folder folder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Đổi tên thư mục");

        final EditText input = new EditText(context);
        input.setText(folder.getName());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                listener.onRenameFolder(folder, newName);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        ImageButton moreButton;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }
}