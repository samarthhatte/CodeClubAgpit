package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView ivEventBanner;
    private TextView tvTitle, tvDate, tvTime, tvLocation, tvDescription;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize Views
        ivEventBanner = findViewById(R.id.ivEventBanner);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);

        db = FirebaseFirestore.getInstance();

        // Get Event ID from Intent
        String eventId = getIntent().getStringExtra("eventId");

        if (eventId != null) {
            fetchEventDetails(eventId);
        } else {
            Toast.makeText(this, "Event data not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchEventDetails(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Bind Data (Matching your DB keys)
                        tvTitle.setText(documentSnapshot.getString("title"));
                        tvDate.setText(documentSnapshot.getString("date"));
                        tvDescription.setText(documentSnapshot.getString("description"));

                        // New fields with defaults
                        String time = documentSnapshot.getString("time");
                        String location = documentSnapshot.getString("location");
                        tvTime.setText(time != null ? time : "TBA");
                        tvLocation.setText(location != null ? location : "AGPIT Campus");

                        String imageUrl = documentSnapshot.getString("imageUrl");
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.img)
                                .into(ivEventBanner);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}