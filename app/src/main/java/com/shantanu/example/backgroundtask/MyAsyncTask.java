package com.shantanu.example.backgroundtask;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.shantanu.example.backgroundtask.MainActivity.IMAGE_PATH;

public class MyAsyncTask extends AsyncTask<String,Integer,Void> {
    private IOnProgressReceived mOnProgressReceived;

    public MyAsyncTask(Context context) {
        this.mOnProgressReceived = (IOnProgressReceived) context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            long totalSizeOfImage = connection.getContentLength();
            long totalSizeOfImageDownloaded = 0;

            File file = new File(IMAGE_PATH);
            if (file.exists()) {
                totalSizeOfImageDownloaded = file.length();
                if (totalSizeOfImageDownloaded < totalSizeOfImage) {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Range", "bytes=" + file.length() + "-");
                } else if (totalSizeOfImageDownloaded == totalSizeOfImage) {
                    publishProgress(100);
                    return null;
                }
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.connect();
            InputStream stream = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(IMAGE_PATH, true);

            int count;
            byte[] image_buffer = new byte[10];

            while ((count = stream.read(image_buffer)) != -1) {
                totalSizeOfImageDownloaded += count;
                int progress = (int) ((totalSizeOfImageDownloaded * 100) / totalSizeOfImage);
                publishProgress(progress);
                fos.write(image_buffer, 0, count);
            }

            connection.disconnect();
            fos.flush();
            fos.close();
            stream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mOnProgressReceived != null) {
            mOnProgressReceived.sendProgress(values[0]);
        }
    }

    interface IOnProgressReceived {
        void sendProgress(int progress);
    }
}
