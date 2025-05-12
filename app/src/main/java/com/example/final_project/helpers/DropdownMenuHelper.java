package com.example.final_project.helpers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.activity.SettingActivity;
import com.example.final_project.managers.FileTypeManager;

public class DropdownMenuHelper {
    private MainActivity activity;
    private PopupWindow popupWindow;
    private FileTypeManager fileTypeManager;

    public DropdownMenuHelper(MainActivity activity) {
        this.activity = activity;
        this.fileTypeManager = new FileTypeManager(activity);
    }

    public void showDropdownMenu() {
        // Inflate the layout for the popup window
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dropdown_menu, null);

        // Lấy kích thước màn hình
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(displayMetrics.widthPixels * 0.75); // Lấy 75% chiều rộng màn hình

        // Create the popup window với chiều rộng đã tính
        popupWindow = new PopupWindow(
                popupView,
                width,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        // Set background drawable to allow dismissing when clicking outside
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        // Add animation for slide from left
        popupWindow.setAnimationStyle(R.style.SlideLeftPopupAnimation);

        // Set up click listeners
        ImageButton closeButton = popupView.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Thiết lập các button event
        setupFileTypeButtons(popupView);
        setupOptionButtons(popupView);

        // Show the popup window at the LEFT side
        popupWindow.showAtLocation(activity.findViewById(android.R.id.content),
                Gravity.START | Gravity.TOP,
                0,
                0);
    }

    private void setupFileTypeButtons(View popupView) {
        // PDF button
        TextView btnPDF = popupView.findViewById(R.id.btn_pdf);
        btnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeManager.handleFileTypeSelection("pdf");
                popupWindow.dismiss();
            }
        });

        // DOC button
        TextView btnDOC = popupView.findViewById(R.id.btn_doc);
        btnDOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeManager.handleFileTypeSelection("docx");
                popupWindow.dismiss();
            }
        });

        // TXT button
        TextView btnTXT = popupView.findViewById(R.id.btn_txt);
        btnTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeManager.handleFileTypeSelection("txt");
                popupWindow.dismiss();
            }
        });

        // XLS button
        TextView btnXLS = popupView.findViewById(R.id.btn_xls);
        btnXLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeManager.handleFileTypeSelection("xlsx");
                popupWindow.dismiss();
            }
        });

        // PPT button
        TextView btnPPT = popupView.findViewById(R.id.btn_ppt);
        btnPPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeManager.handleFileTypeSelection("pptx");
                popupWindow.dismiss();
            }
        });

        // ZIP button
        TextView btnZIP = popupView.findViewById(R.id.btn_zip);
        btnZIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeManager.handleFileTypeSelection("zip");
                popupWindow.dismiss();
            }
        });
    }

    private void setupOptionButtons(View popupView) {
        // Implementation for setupOptionButtons
        // This method was called in the original code but not implemented
        // You would likely want to add event handlers for the sync, settings, and help options here

//        View syncLayout = popupView.findViewById(R.id.syncLayout);
//        syncLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle sync button click
//                popupWindow.dismiss();
//            }
//        });

        View settingsLayout = popupView.findViewById(R.id.settingsLayout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang SettingActivity
                v.getContext().startActivity(new Intent(v.getContext(), SettingActivity.class));
                // Đóng PopupWindow
                popupWindow.dismiss();
            }
        });

//        View helpLayout = popupView.findViewById(R.id.helpLayout);
//        helpLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle help button click
//                popupWindow.dismiss();
//            }
//        });
    }
}