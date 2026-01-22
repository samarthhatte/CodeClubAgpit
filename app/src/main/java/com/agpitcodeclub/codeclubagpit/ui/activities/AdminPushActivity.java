package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPushActivity extends AppCompatActivity {

    private static final String TAG = "ADMIN_PUSH";

    private Spinner spinnerEvents;
    private FirebaseFirestore db;

    private final List<String> eventTitles = new ArrayList<>();
    private final List<String> eventIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_push);

        db = FirebaseFirestore.getInstance();
        spinnerEvents = findViewById(R.id.spinnerEvents);

        loadEvents();

        findViewById(R.id.btnSendPush).setOnClickListener(v -> {
            Log.d(TAG, "STEP 1: Send button clicked");

            if (eventTitles.isEmpty()) {
                Toast.makeText(this, "Events still loading…", Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = spinnerEvents.getSelectedItemPosition();
            Log.d(TAG, "STEP 2: Spinner position = " + pos);

            if (pos >= 0) {
                triggerPush(eventTitles.get(pos), eventIds.get(pos));
            }
        });
    }

    // ----------------------------------
    // Load events into spinner
    // ----------------------------------
    private void loadEvents() {
        Log.d(TAG, "Loading events from Firestore");

        db.collection("events")
                .get()
                .addOnSuccessListener(snapshots -> {
                    eventTitles.clear();
                    eventIds.clear();

                    for (DocumentSnapshot doc : snapshots) {
                        eventTitles.add(doc.getString("title"));
                        eventIds.add(doc.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            eventTitles
                    );
                    spinnerEvents.setAdapter(adapter);

                    Log.d(TAG, "Events loaded: " + eventTitles.size());
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to load events", e)
                );
    }

    // ----------------------------------
    // Trigger push notification
    // ----------------------------------
    private void triggerPush(String title, String eventId) {

        Log.d(TAG, "STEP 3: triggerPush()");
        Log.d(TAG, "STEP 4: Event title = " + title);
        Log.d(TAG, "STEP 5: Event ID = " + eventId);

        String url = "https://scaling-trust-ai.onrender.com/send-notification";

        // 🔥 Fetch imageUrl from Firestore FIRST
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {

                    String imageUrl = doc.getString("imageUrl"); // may be null
                    Log.d(TAG, "STEP 6: imageUrl = " + imageUrl);

                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("title", "New Event: " + title);
                        jsonBody.put("body", "Check out the details for " + title);

                        // Image is OPTIONAL
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            jsonBody.put("image", imageUrl);
                        }

                        jsonBody.put("topic", "all_members");

                        Log.d(TAG, "STEP 7: JSON = " + jsonBody.toString());

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON creation failed", e);
                        return;
                    }

                    sendVolleyRequest(url, jsonBody);
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to fetch event image", e)
                );
    }

    // ----------------------------------
    // Volley request sender
    // ----------------------------------
    private void sendVolleyRequest(String url, JSONObject jsonBody) {

        Log.d(TAG, "STEP 8: Sending Volley request");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d(TAG, "STEP 9: SUCCESS");
                    Log.d(TAG, "Response = " + response.toString());
                    Toast.makeText(this, "Notification sent!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e(TAG, "STEP 9: ERROR", error);
                    Toast.makeText(this, "Push failed – check logs", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(
                new com.android.volley.DefaultRetryPolicy(
                        60000,
                        0,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        Volley.newRequestQueue(this).add(request);

        Log.d(TAG, "STEP 10: Request added to queue");
    }
}
