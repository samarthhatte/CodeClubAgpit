package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class EventDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        ImageView ivEventBanner = findViewById(R.id.ivEventBanner);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvDescription = findViewById(R.id.tvDescription);

        String eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
            Toast.makeText(this, "Invalid Event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {

                        tvTitle.setText(doc.getString("title"));
                        tvDate.setText(doc.getString("date"));
                        tvDescription.setText(doc.getString("description"));

                        String imageUrl = doc.getString("imageUrl");

                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.event_placeholder) // optional
                                .error(R.drawable.event_placeholder)       // optional
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivEventBanner);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show()
                );
    }
}
