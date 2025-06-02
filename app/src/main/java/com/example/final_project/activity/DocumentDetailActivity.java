package com.example.final_project.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.models.TTSReponse;
import com.example.final_project.networks.BotApiServices;
import com.example.final_project.networks.ConfigApiServices;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.FPTClient;
import com.example.final_project.networks.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentDetailActivity extends AppCompatActivity {

    private static final String EXTRA_DOCUMENT_TITLE = "document_title";
    private static final String EXTRA_DOCUMENT_TYPE = "document_type";
    private static final String EXTRA_FILE_URL = "file_url";
    private static final String EXTRA_DOCUMENT_ID = "document_id";

    private TextView documentTitleText, progressText;
    private ImageButton backButton, seekBarlabel;
    private SeekBar seekBar;
    private FloatingActionButton aiButton;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private boolean isPlay;
    private BotApiServices apiServices = RetrofitClient.getBotApiService(this);
    private ConfigApiServices configApiServices = RetrofitClient.getConfigApiService(this);
    private DocumentApiServices documentApiServices = RetrofitClient.getDocumentApiService(this);
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
        progressText = findViewById(R.id.progressText);
        seekBar = findViewById(R.id.seekBar);
        seekBarlabel = findViewById(R.id.seekBarLabel);
        handler = new Handler();
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

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiDialog.dismiss();
                showSearchDialog();
                // Add logic to handle content search
            }
        });

        textToSpeechLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractText(getIntent().getStringExtra(EXTRA_DOCUMENT_ID));
                aiDialog.dismiss();
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

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tìm kiếm nội dung");
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null);
        builder.setView(view);

        builder.setPositiveButton("Tìm", (dialog, which) -> {
            TextView input = view.findViewById(R.id.searchInput);
            String query = input.getText().toString().trim();
            if (!query.isEmpty()) {
                performSemanticSearch(query);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void performSemanticSearch(String question)
    {
        String id = getIntent().getStringExtra(EXTRA_DOCUMENT_ID);
        JsonObject questionObj = new JsonObject();
        questionObj.addProperty("question", question);
        Call<JsonObject> call = apiServices.semanticSearch(questionObj, id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String matchedText = response.body().get("result").getAsString();
                    Log.d("Text Match", matchedText);
                    scrollToMatchedText(matchedText);
                } else {
                    Toast.makeText(DocumentDetailActivity.this, "Không tìm thấy kết quả phù hợp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DocumentDetailActivity.this, "Lỗi tìm kiếm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void scrollToMatchedText(String text) {
        WebView webView = findViewById(R.id.webView);
        // Bước 1: Loại bỏ số dòng đầu mỗi đoạn, ví dụ "123. ", "1. "
        String noLineNumbers = text.replaceAll("^\\d{1,4}\\.\\s*", "")       // cho dòng đầu
                .replaceAll("\\n\\d{1,4}\\.\\s*", "\n");  // cho các dòng tiếp theo

        // Bước 2: Escape để an toàn nhúng vào JavaScript
        String safeText = noLineNumbers
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "")
                .replace("\t", "");
        String js = "javascript:if(window.find('" + safeText + "')){" +
                "window.getSelection().anchorNode.parentNode.scrollIntoView();" +
                "}";
        Log.d("Js", js);
        webView.evaluateJavascript(js, null);
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
    private void extractText(String id) {
        Call<JsonObject> call = documentApiServices.extractText(id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String text = response.body().get("text").getAsString(); // sửa theo key bạn dùng
                    if (!text.isEmpty()) {
                        readText(text); // Gửi nội dung để xử lý TTS
                    } else {
                        Toast.makeText(DocumentDetailActivity.this, "Không có nội dung để đọc", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DocumentDetailActivity.this, "Lỗi trích xuất văn bản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DocumentDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readText(String text) {
        Call<JsonObject> call = configApiServices.getConfig();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String key = response.body().get("fpt_api_key").getAsString();
                    Log.d("Key", key);
                    isPlay = true;
                    getAIRead(text, key, isPlay); // Đưa text và apiKey vào convert
                } else {
                    Toast.makeText(DocumentDetailActivity.this, "Không lấy được API Key", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DocumentDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getAIRead(String text, String apiKey, boolean initialPlayState) {
        FPTClient.getClient().convertTextToSpeech(text, apiKey)
                .enqueue(new Callback<TTSReponse>() {
                    @Override
                    public void onResponse(Call<TTSReponse> call, Response<TTSReponse> response) {
                        if (response.isSuccessful()) {
                            try {
                                TTSReponse TTSmodel = response.body();
                                mediaPlayer = new MediaPlayer();
                                Log.d("API_RESPONSE", TTSmodel.getAsync());
                                mediaPlayer.setDataSource(TTSmodel.getAsync());
                                mediaPlayer.prepareAsync();

                                mediaPlayer.setOnPreparedListener(mp -> {
                                    seekBar.setMax(mp.getDuration());
                                    progressText.setText(formatTime(mp.getDuration()));
                                    mp.start();
                                    isPlay = true; // Set initial state
                                    seekBarlabel.setImageResource(R.drawable.btnpause); // Set initial icon
                                    updateSeekBar();
                                });

                                mediaPlayer.setOnCompletionListener(mp -> {
                                    seekBar.setProgress(0);
                                    isPlay = false; // Reset state on completion
                                    seekBarlabel.setImageResource(R.drawable.btnplay); // Update icon
                                });

                                // Single OnClickListener for play/pause toggle
                                seekBarlabel.setOnClickListener(v -> {
                                    if (mediaPlayer != null) {
                                        if (isPlay) {
                                            mediaPlayer.pause();
                                            isPlay = false;
                                            seekBarlabel.setImageResource(R.drawable.btnplay);
                                        } else {
                                            mediaPlayer.start();
                                            isPlay = true;
                                            seekBarlabel.setImageResource(R.drawable.btnpause);
                                            updateSeekBar(); // Resume updating SeekBar
                                        }
                                    }
                                });

                                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        if (fromUser && mediaPlayer != null) {
                                            mediaPlayer.seekTo(progress);
                                        }
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {}
                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {}
                                });

                            } catch (Exception e) {
                                Log.d("MEDIA_PLAYER_ERROR", e.getMessage());
                                Toast.makeText(DocumentDetailActivity.this, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("TTS", response.message());
                            Toast.makeText(DocumentDetailActivity.this, "Lỗi khi chuyển văn bản thành giọng nói " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TTSReponse> call, Throwable t) {
                        Toast.makeText(DocumentDetailActivity.this, "Lỗi kết nối với FPT.AI", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            progressText.setText(formatTime(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private String formatTime(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }


    public void playAudioFromUrl(String audioUrl) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync(); // Asynchronous để không block UI
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
        }
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