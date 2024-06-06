package com.example.unilink;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Boolean> {
    private final Context context;
    private String name;

    public DownloadImageTask(Context context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        String downloadUrlOfImage = urls[0];
        try {
            URL url = new URL(downloadUrlOfImage);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            File file = new File(context.getFilesDir(), name);
            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(file);

            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.close();
            input.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}