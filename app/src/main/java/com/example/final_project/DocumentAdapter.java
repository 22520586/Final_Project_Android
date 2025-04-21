package com.example.final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private Context context;
    private List<Document> documentList;

    public DocumentAdapter(Context context, List<Document> documentList) {
        this.context = context;
        this.documentList = documentList;
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

        holder.moreButton.setOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn nút more
            Toast.makeText(context, "Tùy chọn cho: " + document.getTitle(), Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn vào item
            Toast.makeText(context, "Đã chọn: " + document.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, titleText;
        ImageButton moreButton;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.documentTypeText);
            titleText = itemView.findViewById(R.id.documentTitleText);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }
}
