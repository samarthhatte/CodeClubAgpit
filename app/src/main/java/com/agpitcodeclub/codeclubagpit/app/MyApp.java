package com.agpitcodeclub.codeclubagpit.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 🌙 Night Mode (System Based)
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        );

        // ☁️ Cloudinary init
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dc67ajheo");

        MediaManager.init(this, config);
    }
}
