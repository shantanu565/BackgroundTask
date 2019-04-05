package com.shantanu.example.backgroundtask;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.shantanu.example.backgroundtask.MainActivity.IMAGE_PATH;
import static com.shantanu.example.backgroundtask.MainActivity.IMAGE_URL;

public class MyIntentService extends IntentService implements NetworkCheckReceiver.IOnNetworkConnected {
    ResultReceiver resultReceiver1;
    NetworkCheckReceiver networkReceiver;

    public MyIntentService() {
        super("ServiceExample");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        resultReceiver1 = intent.getParcelableExtra("receiver");
        networkReceiver = new NetworkCheckReceiver((NetworkCheckReceiver.IOnNetworkConnected) this);
        downloadImage();
    }

    private void downloadImage() {
        try {
            URL url = new URL(IMAGE_URL);
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
                    Bundle bundle = new Bundle();
                    bundle.putInt("progress", 100);
                    resultReceiver1.send(101, bundle);
                    return;
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
                Bundle bundle = new Bundle();
                bundle.putInt("progress", progress);
                resultReceiver1.send(101, bundle);
                fos.write(image_buffer, 0, count);
            }

            connection.disconnect();
            fos.flush();
            fos.close();
            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void isNetworkConnected(boolean network_state) {
        if(!network_state){
            stopSelf();
        }
    }

}
