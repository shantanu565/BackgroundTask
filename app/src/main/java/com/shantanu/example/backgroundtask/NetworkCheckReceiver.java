package com.shantanu.example.backgroundtask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheckReceiver extends BroadcastReceiver {
    ConnectivityManager connectivityManager;
    IOnNetworkConnected networkConnected;

    public NetworkCheckReceiver(){

    }
    public NetworkCheckReceiver(IOnNetworkConnected networkConnected) {
        this.networkConnected = networkConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkConnected != null) {
                networkConnected.isNetworkConnected(true);
            }
        } else {
            if (networkConnected != null) {
                networkConnected.isNetworkConnected(false);
            }
    }


}
    interface IOnNetworkConnected {
        void isNetworkConnected(boolean network_state);}}
