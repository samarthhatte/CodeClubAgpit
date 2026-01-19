package com.agpitcodeclub.codeclubagpit;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dc67ajheo");

        MediaManager.init(this, config);
    }
}
