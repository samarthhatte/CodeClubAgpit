package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.core.view.WindowCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.bumptech.glide.Glide;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);


        ImageView img = findViewById(R.id.imgFull);
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        Glide.with(this)
                .load(imageUrl)
                .into(img);
    }
}
