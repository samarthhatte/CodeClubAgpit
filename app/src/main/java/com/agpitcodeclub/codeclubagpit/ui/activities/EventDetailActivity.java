package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.agpitcodeclub.codeclubagpit.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView ivEventBanner, ivBack;
    private TextView tvTitle, tvDate, tvTime, tvLocation, tvDescription;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Makes the banner look premium by going under the status bar
        setContentView(R.layout.activity_event_detail);

        // Initialize Views
        ivEventBanner = findViewById(R.id.ivEventBanner);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);

        // Setup Back Button (Ensure you add an ImageView with id ivBack in your XML)
        ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        db = FirebaseFirestore.getInstance();

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
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // Use the precise keys from your Firestore
                        tvTitle.setText(doc.getString("title"));
                        tvDate.setText(doc.getString("date"));
                        tvDescription.setText(doc.getString("description"));

                        String time = doc.getString("time");
                        String location = doc.getString("location");
                        tvTime.setText(time != null ? time : "TBA");
                        tvLocation.setText(location != null ? location : "AGPIT Campus");

                        String imageUrl = doc.getString("imageUrl");

// 🟢 FIX: Check if imageUrl is null before giving it to Glide
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.img)
                                    .error(R.drawable.ic_event_note) // Fallback if link is broken
                                    .centerCrop()
                                    .into(ivEventBanner);
                        } else {
                            // 🟡 If there is no image in DB, show a default icon/placeholder
                            ivEventBanner.setImageResource(R.drawable.ic_event_note);
                            ivEventBanner.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Don't stretch the icon
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}