package com.app.harcdis.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.app.harcdis.screens.SplashScreen;


public class Sp {



    public static void write_shared_pref(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.tcp_haryana, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    public static String read_shared_pref(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.tcp_haryana, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);

    }

    public static void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.tcp_haryana, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        editor.apply();


        Intent refresh = new Intent(context, SplashScreen.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(refresh);
        Toast.makeText(context, "Successfully Logged Out", Toast.LENGTH_SHORT).show();

    }
}
