package com.agpitcodeclub.codeclubagpit;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        ImageView img = findViewById(R.id.imgFull);
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        Glide.with(this)
                .load(imageUrl)
                .into(img);
    }
}
