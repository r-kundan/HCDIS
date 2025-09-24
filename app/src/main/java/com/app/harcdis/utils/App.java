package com.app.harcdis.utils;

import android.app.Application;


import androidx.appcompat.app.AppCompatDelegate;
public class App extends Application {
    @Override
    public void onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate();


    }
}
