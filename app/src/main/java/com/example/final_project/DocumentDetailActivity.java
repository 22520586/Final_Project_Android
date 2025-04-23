package com.example.final_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        // AI button shows a toast message
        aiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentDetailActivity.this,
                        "Trợ lý AI đang được khởi động...", Toast.LENGTH_SHORT).show();
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