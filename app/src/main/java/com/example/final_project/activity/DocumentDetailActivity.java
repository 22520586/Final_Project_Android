package com.example.final_project.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.networks.BotApiServices;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.net.URLEncoder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentDetailActivity extends AppCompatActivity {

    private static final String EXTRA_DOCUMENT_TITLE = "document_title";
    private static final String EXTRA_DOCUMENT_TYPE = "document_type";
    private static final String EXTRA_FILE_URL = "file_url";
    private static final String EXTRA_DOCUMENT_ID = "document_id";

    private TextView documentTitleText;
    private ImageButton backButton;
    private FloatingActionButton aiButton;
    private BotApiServices apiServices = RetrofitClient.getBotApiService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        // Initialize views
        initializeViews();

        // Get document information from intent
        String documentTitle = getIntent().getStringExtra(EXTRA_DOCUMENT_TITLE);
        String documentType = getIntent().getStringExtra(EXTRA_DOCUMENT_TYPE);
        String documentUrl = getIntent().getStringExtra(EXTRA_FILE_URL);
        String documentId = getIntent().getStringExtra(EXTRA_DOCUMENT_ID);

        try {
            WebView webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            String viewerUrl = "https://docs.google.com/gview?embedded=true&url=" + documentUrl;
            webView.loadUrl(viewerUrl);

            Log.d("viewerUrl", viewerUrl);
            // Set document title
            if (documentTitle != null) {
                documentTitleText.setText(documentTitle);
            }

            // Setup button listeners
            setupButtonListeners();
        } catch (Exception e) {
            Toast.makeText(this, "lỗi" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Merror: ", e.getMessage());
        }
    }

    private void initializeViews() {
        documentTitleText = findViewById(R.id.documentTitleText);
        backButton = findViewById(R.id.backButton);
        aiButton = findViewById(R.id.aiButton);
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
    }

    // Method to show the AI features dialog
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
                summarizeText(getIntent().getStringExtra(EXTRA_DOCUMENT_ID));
                aiDialog.dismiss();
                // Add logic to handle document summarization
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

    private void summarizeText(String id) {
        Call<JsonObject> call = apiServices.summarizeDocument(id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful())
                {
                    String summary = response.body().get("message").toString();
                    showSummaryDialog(summary);
                } else {
                    Toast.makeText(DocumentDetailActivity.this, "Lỗi khi tóm tắt tài liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DocumentDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSummaryDialog(String summary) {
        new AlertDialog.Builder(this)
                .setTitle("Tóm tắt tài liệu")
                .setMessage(summary)
                .setPositiveButton("Đóng", null)
                .show();
    }

    /**
     * Static method to create intent for launching this activity
     */
    public static Intent newIntent(Context context, String documentTitle, String documentType, String fileUrl, String id) {
        Intent intent = new Intent(context, DocumentDetailActivity.class);
        Log.d("DocumentDetailActivity", "Creating intent with title: " + fileUrl);
        intent.putExtra(EXTRA_FILE_URL, fileUrl); // Add fileUrl to the intent)
        intent.putExtra(EXTRA_DOCUMENT_TITLE, documentTitle);
        intent.putExtra(EXTRA_DOCUMENT_TYPE, documentType);
        intent.putExtra(EXTRA_DOCUMENT_ID, id);
        intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }
}