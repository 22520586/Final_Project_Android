package com.example.final_project.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DocumentDetailActivity extends AppCompatActivity {

    private static final String EXTRA_DOCUMENT_TITLE = "document_title";
    private static final String EXTRA_DOCUMENT_TYPE = "document_type";

    private TextView documentTitleText;
    private ImageButton backButton;
    private FloatingActionButton aiButton;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        // Initialize views
        initializeViews();

        // Get document information from intent
        String documentTitle = getIntent().getStringExtra(EXTRA_DOCUMENT_TITLE);
        String documentType = getIntent().getStringExtra(EXTRA_DOCUMENT_TYPE);

        // Set document title
        if (documentTitle != null) {
            documentTitleText.setText(documentTitle);
        }

        // Setup button listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        documentTitleText = findViewById(R.id.documentTitleText);
        backButton = findViewById(R.id.backButton);
        aiButton = findViewById(R.id.aiButton);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupButtonListeners() {
        // Back button returns to MainActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // AI button shows a dialog with AI feature options
        aiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAIFeaturesDialog();
            }
        });

        // Bottom navigation setup
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    finish(); // Return to main screen
                    return true;
                } else if (id == R.id.nav_folder) {
                    Toast.makeText(DocumentDetailActivity.this,
                            "Mở thư mục", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_settings) {
                    Toast.makeText(DocumentDetailActivity.this,
                            "Mở cài đặt", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    // Method to show the AI features dialog - moved outside of the onClick method
    private void showAIFeaturesDialog() {
        // Create dialog
        final Dialog aiDialog = new Dialog(DocumentDetailActivity.this);
        aiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        aiDialog.setContentView(R.layout.dialog_ai_features);

        // Make dialog width match parent
        Window window = aiDialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }

        // Get views
        ImageButton closeButton = aiDialog.findViewById(R.id.btn_close_dialog);
        View summarizeLayout = aiDialog.findViewById(R.id.layout_summarize);
        View searchLayout = aiDialog.findViewById(R.id.layout_search);
        View textToSpeechLayout = aiDialog.findViewById(R.id.layout_text_to_speech);
        View questionsLayout = aiDialog.findViewById(R.id.layout_questions);

        // Set close button click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiDialog.dismiss();
            }
        });

        // Set click listeners for feature items
        summarizeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentDetailActivity.this,
                        "Đang tóm tắt tài liệu...", Toast.LENGTH_SHORT).show();
                aiDialog.dismiss();
                // Add logic to handle document summarization
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentDetailActivity.this,
                        "Đang mở tính năng tìm kiếm...", Toast.LENGTH_SHORT).show();
                aiDialog.dismiss();
                // Add logic to handle content search
            }
        });

        textToSpeechLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentDetailActivity.this,
                        "Tính năng này không khả dụng", Toast.LENGTH_SHORT).show();
                // Do not dismiss dialog as feature is unavailable
            }
        });

        questionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentDetailActivity.this,
                        "Đang mở trợ lý AI...", Toast.LENGTH_SHORT).show();
                aiDialog.dismiss();
                // Add logic to handle AI questioning
            }
        });

        aiDialog.show();
    }

    /**
     * Static method to create intent for launching this activity
     */
    public static Intent newIntent(Context context, String documentTitle, String documentType) {
        Intent intent = new Intent(context, DocumentDetailActivity.class);
        intent.putExtra(EXTRA_DOCUMENT_TITLE, documentTitle);
        intent.putExtra(EXTRA_DOCUMENT_TYPE, documentType);
        return intent;
    }
}