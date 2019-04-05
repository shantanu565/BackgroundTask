package com.shantanu.example.backgroundtask;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity  implements MyAsyncTask.IOnProgressReceived, NetworkCheckReceiver.IOnNetworkConnected, ProgressHandler.IOnResultProgressReceived{
    ImageView imageView;
    Button btnAsync,btnService,btnClear;
    static final String IMAGE_URL="https://homepages.cae.wisc.edu/~ece533/images/airplane.png";
    static final String IMAGE_PATH = "/data/data/com.shantanu.example.backgroundtask/example1.jpg";
    MyAsyncTask mAsyncTaskExample;
    ProgressBar progressBar;
    TextView txtProgrss;
    NetworkCheckReceiver mNetworkReceiver;
    Intent serviceIntent;
    boolean isConnected, serviceStarted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.iv);
        btnAsync=findViewById(R.id.btn_asyn);
        btnService=findViewById(R.id.btn_service);
        btnClear=findViewById(R.id.btn_clear);
        progressBar=findViewById(R.id.progreebar);
        txtProgrss=findViewById(R.id.progres_tv);

        progressBar.setMax(100);
        mNetworkReceiver = new NetworkCheckReceiver((NetworkCheckReceiver.IOnNetworkConnected) this);

        btnAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtons();
                mAsyncTaskExample = new MyAsyncTask(v.getContext());
                if (isConnected) {
                    mAsyncTaskExample.execute(IMAGE_URL);
                } else {
                    Toast.makeText(MainActivity.this, "Please Connect to Network", Toast.LENGTH_SHORT).show();
                    enableAllButtons();
                }
            }
        });

        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtons();
                if (isConnected) {
                    serviceIntent = new Intent(MainActivity.this, MyIntentService.class);
                    ProgressHandler resultHandler = new ProgressHandler(new Handler());
                    resultHandler.setmOnProgressReceived(v.getContext());
                    serviceIntent.putExtra("receiver",resultHandler);
                    serviceStarted = true;
                    startService(serviceIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Please Connect to Network", Toast.LENGTH_SHORT).show();
                    enableAllButtons();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(IMAGE_PATH);
                if (file.exists()) {
                    file.delete();
                    imageView.setImageDrawable(null);
                } else
                    Toast.makeText(MainActivity.this, "Image Doesn't Exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableAllButtons() {
       btnAsync.setEnabled(true);
        btnService.setEnabled(true);
        btnClear.setEnabled(true);
    }

    private void disableAllButtons() {
        btnAsync.setEnabled(false);
        btnService.setEnabled(false);
        btnClear.setEnabled(false);
    }

    @Override
    public void sendProgress(int progress) {
       txtProgrss.setText(progress + " %");
        progressBar.setProgress(progress);

        if (progress == 100) {
            imageView.setImageDrawable(Drawable.createFromPath(IMAGE_PATH));
            progressBar.setProgress(0);
            txtProgrss.setText("0 %");
            enableAllButtons();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public void isNetworkConnected(boolean network_state) {
        isConnected = network_state;
        if (network_state && mAsyncTaskExample != null) {
            disableAllButtons();
            mAsyncTaskExample = new MyAsyncTask(this);
            mAsyncTaskExample.execute(IMAGE_URL);
        } else if (network_state && serviceStarted) {
            disableAllButtons();
            startService(serviceIntent);
        } else {
            enableAllButtons();
        }
    }

    @Override
    public void sendProgressFromResult(int progress) {
        txtProgrss.setText(progress + " %");
        progressBar.setProgress(progress);

        if (progress == 100) {
            imageView.setImageDrawable(Drawable.createFromPath(IMAGE_PATH));
            progressBar.setProgress(0);
            txtProgrss.setText("0 %");
            serviceStarted = false;
            enableAllButtons();
        }
    }
}


