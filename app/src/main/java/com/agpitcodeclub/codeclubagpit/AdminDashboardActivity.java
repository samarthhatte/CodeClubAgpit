package com.agpitcodeclub.codeclubagpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        MaterialCardView cardAddEvent = findViewById(R.id.cardAddEvent);
        MaterialCardView cardManageMembers = findViewById(R.id.cardManageMembers);
        MaterialCardView cardNotifications = findViewById(R.id.cardNotifications);

        cardAddEvent.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEventActivity.class));
        });

        cardManageMembers.setOnClickListener(v -> {
            Intent intent = new Intent(this, MembersActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        });

        // Trigger the Tagging Dialog when Notifications card is clicked
        cardNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminPushActivity.class));
        });


        // Inside onCreate
        findViewById(R.id.btnAdminLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            // Clear the activity stack so they can't go back
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showNotificationDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> eventTitles = new ArrayList<>();
        List<String> eventIds = new ArrayList<>();

        // 1. Fetch existing events from Firestore
        db.collection("events").get().addOnSuccessListener(snapshots -> {
            for (DocumentSnapshot doc : snapshots) {
                eventTitles.add(doc.getString("title"));
                eventIds.add(doc.getId()); // Store ID for tagging
            }

            if (eventTitles.isEmpty()) {
                Toast.makeText(this, "No events found to tag!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Selection Dialog for Tagging
            String[] titlesArray = eventTitles.toArray(new String[0]);
            new AlertDialog.Builder(this)
                    .setTitle("Select Event to Alert Members")
                    .setItems(titlesArray, (dialog, which) -> {
                        String selectedTitle = titlesArray[which];
                        String selectedId = eventIds.get(which);

                        // 3. Trigger Push with Tagged ID
                        sendEventPush(selectedTitle, selectedId);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void sendEventPush(String title, String eventId) {
        // Tagging logic: This ID can be used for deep-linking in the notification
        Log.d("FCM_PUSH", "Tagging Event ID: " + eventId);
        Toast.makeText(this, "Broadcast Pushed for: " + title, Toast.LENGTH_LONG).show();
    }
}