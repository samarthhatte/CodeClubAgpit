package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.bumptech.glide.Glide;
import androidx.core.view.WindowCompat;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        EdgeToEdge.enable(this);

        PhotoView photoView = findViewById(R.id.photoView);

        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        Glide.with(this)
                .load(imageUrl)
                .into(photoView);
    }
}

