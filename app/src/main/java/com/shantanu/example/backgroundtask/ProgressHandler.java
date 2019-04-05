package com.shantanu.example.backgroundtask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


public class ProgressHandler extends ResultReceiver {
    IOnResultProgressReceived onResultProgressReceived;

    @SuppressLint("RestrictedApi")
    public ProgressHandler(Handler handler) {
        super(handler);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);

        if (resultCode == 101) {
            int progress = resultData.getInt("progress");
            onResultProgressReceived.sendProgressFromResult(progress);
        }
    }

    public void setmOnProgressReceived(Context context) {
        this.onResultProgressReceived = (IOnResultProgressReceived) context;
    }

    interface IOnResultProgressReceived {
        void sendProgressFromResult(int progress);
    }
}
