package com.agpitcodeclub.codeclubagpit.ui.activities;

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
import androidx.core.view.WindowCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.agpitcodeclub.codeclubagpit.R;
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
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        redirectByRole();
        tvWelcome = findViewById(R.id.tvWelcome);

        loadUserName();

        // Logout
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // Notification Channel (Android 8+)
        createNotificationChannel();

        // Subscribe to FCM topic
        FirebaseMessaging.getInstance().subscribeToTopic("all_members")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful()
                            ? "Subscribed to all_members"
                            : "Subscription Failed";
                    Log.d("FCM_DEBUG", msg);
                });

        // Cards
        MaterialCardView cardMembers = findViewById(R.id.cardMembers);
        MaterialCardView cardEvents = findViewById(R.id.cardEvents);
        MaterialCardView cardProfile = findViewById(R.id.cardProfile);
        MaterialCardView cardAboutUs = findViewById(R.id.cardAboutUs);
        MaterialCardView cardGallery = findViewById(R.id.cardGallery);

        cardGallery.setOnClickListener(v -> startActivity(new Intent(this, GalleryActivity.class)));


        cardMembers.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MembersActivity.class)));

        cardEvents.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EventsActivity.class)));

        cardProfile.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        cardAboutUs.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class)));

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }

    private void redirectByRole() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String role = doc.getString("role");

                    if ("admin".equals(role) || "super_admin".equals(role)) {
                        startActivity(new Intent(this, AdminDashboardActivity.class));
                        finish();
                    }
                    // else → stay in MainActivity (student/member)
                });
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,
                        "Notifications Enabled!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Notifications Disabled. You won't see club updates.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Code Club Alerts";
            String description = "Notifications for club events and updates";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel =
                    new NotificationChannel("club_channel", name, importance);
            channel.setDescription(description);

            NotificationManager manager =
                    getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void loadUserName() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String name = doc.getString("name");
                            tvWelcome.setText("Hello, " + name + "!");
                        }
                    });
        }
    }
}
