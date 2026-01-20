package com.agpitcodeclub.codeclubagpit;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvWelcome;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // 1. Initialize Notification Channel (Required for Android 8.0+)
        createNotificationChannel();

        // 2. Subscribe to topic for club-wide notifications
        FirebaseMessaging.getInstance().subscribeToTopic("all_members")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed to all_members" : "Subscription Failed";
                    Log.d("FCM_DEBUG", msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvWelcome = findViewById(R.id.tvWelcome);

        loadUserName();

        // --- Card Initializations and Listeners (Keep your existing code here) ---
        MaterialCardView cardMembers = findViewById(R.id.cardMembers);
        cardMembers.setOnClickListener(v -> startActivity(new Intent(this, MembersActivity.class)));

        // ADD THIS FOR YOUR EVENTS PAGE
        MaterialCardView cardEvents = findViewById(R.id.cardEvents);
        cardEvents.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EventsActivity.class)));

        MaterialCardView cardProfile = findViewById(R.id.cardProfile);
        cardProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        MaterialCardView cardAlumni = findViewById(R.id.cardAlumni);
        cardAlumni.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MembersActivity.class);
            intent.putExtra("OPEN_TAB_INDEX", 1); // Alumni tab
            startActivity(intent);
        });
        // ... (remaining listeners)

        //check for notification permission for android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications Enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications Disabled. You won't see club updates.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Code Club Alerts";
            String description = "Notifications for club events and updates";
            int importance = NotificationManager.IMPORTANCE_HIGH; // High importance for banners
            NotificationChannel channel = new NotificationChannel("club_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadUserName() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String name = doc.getString("name");
                    tvWelcome.setText("Hello, " + name + "!");
                }
            });
        }
    }


}