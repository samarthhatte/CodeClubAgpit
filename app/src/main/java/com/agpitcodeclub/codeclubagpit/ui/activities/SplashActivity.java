package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;

public class SplashActivity extends AppCompatActivity {

    private static final int LOGO_TIME = 2000;
    private static final int DEDICATION_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        EdgeToEdge.enable(this);

        LinearLayout logoLayout = findViewById(R.id.logoLayout);
        LinearLayout dedicationLayout = findViewById(R.id.dedicationLayout);

        SharedPreferences prefs =
                getSharedPreferences("app_prefs", MODE_PRIVATE);

        boolean firstLaunch = prefs.getBoolean("first_launch", true);

        // ✅ Logo animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoLayout.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (firstLaunch) {

                // Mark shown
                prefs.edit().putBoolean("first_launch", false).apply();

                // Fade out logo
                Animation fadeOut =
                        AnimationUtils.loadAnimation(this, R.anim.fade_out);

                logoLayout.startAnimation(fadeOut);

                // After fade out, hide logo and show dedication
                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    logoLayout.setVisibility(View.GONE);

                    dedicationLayout.setVisibility(View.VISIBLE);

                    // Dedication animation
                    Animation fadeScale =
                            AnimationUtils.loadAnimation(this, R.anim.fade_scale);

                    dedicationLayout.startAnimation(fadeScale);

                    // Move to LoginActivity
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }, DEDICATION_TIME);

                }, 600); // fadeOut duration

            } else {
                // Normal launch
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }

        }, LOGO_TIME);
    }
}
