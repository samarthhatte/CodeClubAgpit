package com.agpitcodeclub.codeclubagpit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        MaterialCardView cardAddEvent = findViewById(R.id.cardAddEvent);
        MaterialCardView cardManageMembers = findViewById(R.id.cardManageMembers);
        MaterialCardView cardNotifications = findViewById(R.id.cardNotifications);


        cardAddEvent.setOnClickListener(v -> {
            // Navigate to Add Event Screen
            startActivity(new Intent(this, AddEventActivity.class));
        });

        cardManageMembers.setOnClickListener(v -> {
            // Navigate to Members List with "Admin Mode" enabled
            Intent intent = new Intent(this, MembersActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        });

        cardNotifications.setOnClickListener(v -> {
            // Screen to send custom FCM alerts
            showNotificationDialog();
        });
    }

    private void showNotificationDialog() {
        // Here you would implement a dialog to send a custom
        // broadcast message to all club members via FCM
        Toast.makeText(this, "Notification system coming soon!", Toast.LENGTH_SHORT).show();
    }
}