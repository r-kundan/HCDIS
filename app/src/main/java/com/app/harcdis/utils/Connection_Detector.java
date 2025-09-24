package com.app.harcdis.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection_Detector {
    private Context context;
    public Connection_Detector(Context context) {
        this.context=context;
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info!=null){
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }
}
