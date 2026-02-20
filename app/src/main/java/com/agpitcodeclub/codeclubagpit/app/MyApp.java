package com.agpitcodeclub.codeclubagpit.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.cloudinary.android.MediaManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 🌙 Night Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // ☁️ Cloudinary init
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dc67ajheo");
        MediaManager.init(this, config);

        // 🔔 FCM Token Update
        updateFCMToken();
        createNotificationChannel();

        // 3. Subscribe to Topic (So admins can reach everyone)
        FirebaseMessaging.getInstance().subscribeToTopic("all_members");

        // 4. Update individual User Token (For Chat)
        updateFCMToken();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "club_channel",
                    "Code Club Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    public void updateFCMToken() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Only proceed if user is logged in
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("FCM_UPDATE", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();

                        // Update Firestore
                        Map<String, Object> tokenData = new HashMap<>();
                        tokenData.put("fcmToken", token);

                        FirebaseFirestore.getInstance().collection("users")
                                .document(userId)
                                .set(tokenData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d("FCM_UPDATE", "Token synced for user: " + userId))
                                .addOnFailureListener(e -> Log.e("FCM_UPDATE", "Token sync failed", e));
                    });
        } else {
            Log.d("FCM_UPDATE", "No user logged in, skipping token update.");
        }
    }
}