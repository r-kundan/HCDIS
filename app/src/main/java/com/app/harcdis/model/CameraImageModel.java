package com.app.harcdis.model;

import android.graphics.Bitmap;

public class CameraImageModel {
    String image_name;
    byte[] image_byte_array;
    Bitmap image_bitmap;


    public CameraImageModel(String image_name, byte[] image_byte_array, Bitmap image_bitmap) {
        this.image_name = image_name;
        this.image_byte_array = image_byte_array;
        this.image_bitmap = image_bitmap;

    }

    public String getImage_name() {
        return image_name;
    }

    public byte[] getImage_byte_array() {
        return image_byte_array;
    }

    public Bitmap getImage_bitmap() {
        return image_bitmap;
    }

}