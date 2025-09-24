package com.app.harcdis.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class UiHelper {
    private static Snackbar snackbar;
    public static final String MyTag = "MyTag";
    public static void showLog(String message) {
        Log.d(MyTag, message);
    }


    public static void showAlertDialog() {

    }



    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static void showSnackBar(Context context,View view, String Message, int ColorId) {
        Toast.makeText(context, "" + Message, Toast.LENGTH_SHORT).show();

    }

    public static void showSnackBarLong(Context context,View view, String Message, int ColorId) {
        Toast.makeText(context, "" + Message, Toast.LENGTH_SHORT).show();

    }

}
