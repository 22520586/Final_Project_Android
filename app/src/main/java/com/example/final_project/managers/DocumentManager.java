package com.example.final_project.managers;

import android.content.Context;

import com.example.final_project.models.Document;

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
        // Tạo dữ liệu ban đầu với thời gian hiện tại
        long currentTime = System.currentTimeMillis();

//        // Tạo các document với thời gian tạo là hiện tại
//        Document doc1 = new Document("PDF", "Báo cáo tài chính Q2");
//        Document doc2 = new Document("DOC", "Đề xuất chính sách mới");
//        Document doc3 = new Document("TXT", "Kết luận cuộc họp");
//        Document doc4 = new Document("XLS", "Dự toán ngân sách");
//
//        // Đảm bảo thời gian tạo được thiết lập
//        doc1.setCreatedAt(currentTime);
//        doc2.setCreatedAt(currentTime);
//        doc3.setCreatedAt(currentTime);
//        doc4.setCreatedAt(currentTime);
//
//        allDocuments.add(doc1);
//        allDocuments.add(doc2);
//        allDocuments.add(doc3);
//        allDocuments.add(doc4);
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
        // Đảm bảo thời gian tạo được thiết lập nếu chưa có
//        if (document.getCreatedAt() == 0) {
//            document.setCreatedAt(System.currentTimeMillis());
//        }
        allDocuments.add(document);
    }

    public void removeDocument(Document document) {
        allDocuments.remove(document);
    }

    public void updateDocument(Document oldDoc, Document newDoc) {
        int index = allDocuments.indexOf(oldDoc);
        if (index != -1) {
            // Giữ lại thời gian tạo từ tài liệu cũ
            newDoc.setCreatedAt(oldDoc.getCreatedAt());

            // Cập nhật thời gian chỉnh sửa
            //newDoc.setUpdatedAt(System.currentTimeMillis());

            allDocuments.set(index, newDoc);
        }
    }

    // Phương thức mới để cập nhật tiêu đề
    public void updateDocumentTitle(Document document, String newTitle) {
        document.setTitle(newTitle);
        //document.setUpdatedAt(System.currentTimeMillis());
    }

    // Phương thức mới để cập nhật đường dẫn
    public void updateDocumentPath(Document document, String newPath) {
        document.setPath(newPath);
        //document.setUpdatedAt(System.currentTimeMillis());
    }

    // Phương thức mới để toggle trạng thái ghim và cập nhật thời gian
    public void toggleDocumentPin(Document document) {
        document.togglePinned();
        //document.setUpdatedAt(System.currentTimeMillis());
    }
}