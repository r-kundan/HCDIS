package com.app.harcdis.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.R;

import java.util.Objects;

public class SimpleImageScreen extends AppCompatActivity {
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_image_screen);
        imageView = findViewById(R.id.image_view);
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("image_url");

        if(!Objects.equals("null",imageUrl)){
            byte[] decodedString2 = Base64.decode(imageUrl, Base64.DEFAULT);
            Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
            imageView.setImageBitmap(decodedByte2);
        }else{
            Toast.makeText(this, "Image Not Found", Toast.LENGTH_SHORT).show();
        }


    }
}