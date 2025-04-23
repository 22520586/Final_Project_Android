package com.example.final_project;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class DocumentManager {
    private Context context;
    private List<Document> allDocuments;

    public DocumentManager(Context context) {
        this.context = context;
        this.allDocuments = new ArrayList<>();
        initializeDocuments();
    }

    private void initializeDocuments() {
        // Tạo dữ liệu ban đầu
        allDocuments.add(new Document("PDF", "Báo cáo tài chính Q2"));
        allDocuments.add(new Document("DOC", "Đề xuất chính sách mới"));
        allDocuments.add(new Document("TXT", "Kết luận cuộc họp"));
        allDocuments.add(new Document("XLS", "Dự toán ngân sách"));
    }

    public List<Document> getInitialDocuments() {
        return new ArrayList<>(allDocuments);
    }

    public List<Document> getPinnedDocuments() {
        List<Document> pinnedDocs = new ArrayList<>();
        for (Document doc : allDocuments) {
            if (doc.isPinned()) {
                pinnedDocs.add(doc);
            }
        }
        return pinnedDocs;
    }

    public List<Document> getDocumentsByType(String fileType) {
        List<Document> filteredDocs = new ArrayList<>();
        for (Document doc : allDocuments) {
            if (doc.getType().equalsIgnoreCase(fileType)) {
                filteredDocs.add(doc);
            }
        }
        return filteredDocs;
    }

    public void addDocument(Document document) {
        allDocuments.add(document);
    }

    public void removeDocument(Document document) {
        allDocuments.remove(document);
    }

    public void updateDocument(Document oldDoc, Document newDoc) {
        int index = allDocuments.indexOf(oldDoc);
        if (index != -1) {
            allDocuments.set(index, newDoc);
        }
    }
}