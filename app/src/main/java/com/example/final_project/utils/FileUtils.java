package com.example.final_project.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static File getFileFromUri(Context context, Uri uri) {
        File file = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String fileName = "upload_" + System.currentTimeMillis();
            File tempFile = new File(context.getCacheDir(), fileName);
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
            file = tempFile;
            Log.d("FileUtils", "File path: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}

