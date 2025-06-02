package com.example.final_project.helpers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.activity.SettingActivity;
import com.example.final_project.models.Document;
import com.example.final_project.models.Tag;
import com.example.final_project.models.User;
import com.example.final_project.networks.DocumentApiServices;
import com.example.final_project.networks.RetrofitClient;
import com.example.final_project.networks.TagApiServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class DropdownMenuHelper {
    private MainActivity activity;
    private PopupWindow popupWindow;
    private DocumentApiServices documentApiServices;
    private TagApiServices tagApiServices;
    private Chip btnPDF, btnDOC, btnTXT, btnXLS, btnPPT, btnZIP;
    private ChipGroup chipGroupTags;
    private List<String> selectedFileTypes = new ArrayList<>();
    private List<String> selectedTags = new ArrayList<>();

    public DropdownMenuHelper(MainActivity activity) {
        this.activity = activity;
        this.documentApiServices = RetrofitClient.getDocumentApiService(activity);
        this.tagApiServices = RetrofitClient.getTagApiService(activity);
    }

    public void showDropdownMenu() {
        // Inflate the layout for the popup window
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dropdown_menu, null);

        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.75); // 75% of screen width

        // Create the popup window
        popupWindow = new PopupWindow(
                popupView,
                width,
                ViewGroup.LayoutParams.MATCH_PARENT, // Use WRAP_CONTENT instead of MATCH_PARENT
                true
        );

        // Set background drawable to allow dismissing when clicking outside
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        // Add animation for slide from left
        popupWindow.setAnimationStyle(R.style.SlideLeftPopupAnimation);

        // Initialize views
        setupFileTypeButtons(popupView);
        setupTagChips(popupView);
        setupOptionButtons(popupView);
        setupSearchButton(popupView);

        // Fetch tags from API
        fetchTags();

        // Show the popup window at the LEFT side
        popupWindow.showAtLocation(activity.findViewById(android.R.id.content),
                Gravity.START | Gravity.TOP,
                0,
                0);
    }

    private String changeFileType(String type)
    {
        switch (type)
        {
            case "PDF":
                return "application/pdf";
            case "DOCX":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "TXT":
                return "text/plain";
            default:
                return type;
        }
    }

    private void setupFileTypeButtons(View popupView) {
        btnPDF = popupView.findViewById(R.id.chipPdf);
        btnDOC = popupView.findViewById(R.id.chipDocx);
        btnTXT = popupView.findViewById(R.id.chipTxt);
        btnXLS = popupView.findViewById(R.id.chipXlsx);
        btnPPT = popupView.findViewById(R.id.chipPptx);
        btnZIP = popupView.findViewById(R.id.chipZip);

        // Add click listeners for file type chips
        List<Chip> fileTypeChips = new ArrayList<>();
        fileTypeChips.add(btnPDF);
        fileTypeChips.add(btnDOC);
        fileTypeChips.add(btnTXT);
        fileTypeChips.add(btnXLS);
        fileTypeChips.add(btnPPT);
        fileTypeChips.add(btnZIP);

        for (Chip chip : fileTypeChips) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String type = changeFileType(chip.getText().toString());
                if (isChecked) {

                    selectedFileTypes.add(type);
                } else {
                    selectedFileTypes.remove(type);
                }
            });
        }
    }

    private void setupTagChips(View popupView) {
        chipGroupTags = popupView.findViewById(R.id.chipGroupTags);
    }

    private void fetchTags() {
        Call<List<Tag>> call = tagApiServices.getAllTags();
        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tag> tags = response.body();
                    populateTagChips(tags);
                } else {
                    activity.showToast("Lỗi khi tải thẻ: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                activity.showToast("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void filterDocuments(String fileType, String tags) {
        Call<List<Document>> call = documentApiServices.filterDocument(tags, fileType);
        call.enqueue(new Callback<List<Document>>() {
            @Override
            public void onResponse(Call<List<Document>> call, Response<List<Document>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.updateFilteredDocuments(response.body());
                    selectedFileTypes.clear(); // Reset selections
                    selectedTags.clear();
                    // Uncheck all chips
                    for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                        Chip chip = (Chip) chipGroupTags.getChildAt(i);
                        chip.setChecked(false);
                    }
                    List<Chip> fileTypeChips = new ArrayList<>();
                    fileTypeChips.add(btnPDF);
                    fileTypeChips.add(btnDOC);
                    fileTypeChips.add(btnTXT);
                    fileTypeChips.add(btnXLS);
                    fileTypeChips.add(btnPPT);
                    fileTypeChips.add(btnZIP);
                    for (Chip chip : fileTypeChips) {
                        chip.setChecked(false);
                    }
                    activity.showToast("Có " + response.body().size() + " tài liệu phù hợp!");
                } else {
                    activity.showToast("Không tìm thấy tài liệu");
                }
            }

            @Override
            public void onFailure(Call<List<Document>> call, Throwable t) {
                activity.showToast("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void populateTagChips(List<Tag> tags) {
        chipGroupTags.removeAllViews(); // Clear existing chips
        for (Tag tag : tags) {
            Chip chip = new Chip(activity);
            chip.setText(tag.getTitle()); // Use getName() instead of getTitle()
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true); // Show check icon when selected
            chip.setChipBackgroundColorResource(R.color.blue_primary);
            chip.setTextColor(activity.getResources().getColor(R.color.white));
            chip.setTextSize(16);
            chip.setChipStrokeWidth(1);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String tagName = tag.getTitle();
                if (isChecked) {
                    selectedTags.add(tagName);
                } else {
                    selectedTags.remove(tagName);
                }
                Log.d("Tag", "Selected tags: " + selectedTags);
            });
            chipGroupTags.addView(chip);
        }
    }

    private void setupOptionButtons(View popupView) {
        // Settings option
        View settingsLayout = popupView.findViewById(R.id.settingsLayout);
        settingsLayout.setOnClickListener(v -> {

            Intent intent = new Intent(activity, SettingActivity.class);

            activity.startActivity(intent);
            popupWindow.dismiss();
        });
    }
    private void setupSearchButton(View popupView) {
        Button searchButton = popupView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String fileType = selectedFileTypes.isEmpty() ? "" : String.join(",", selectedFileTypes);
            String tags = selectedTags.isEmpty() ? "" : String.join(",", selectedTags);
            filterDocuments(fileType, tags);
            popupWindow.dismiss();
        });
    }
    public List<String> getSelectedFileTypes() {
        return selectedFileTypes;
    }

    public List<String> getSelectedTags() {
        return selectedTags;
    }
}