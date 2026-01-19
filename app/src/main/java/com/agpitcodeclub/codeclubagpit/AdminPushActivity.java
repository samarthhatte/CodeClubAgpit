package com.agpitcodeclub.codeclubagpit;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        Log.d(TAG, "onCreate() started");

        db = FirebaseFirestore.getInstance();
        spinnerEvents = findViewById(R.id.spinnerEvents);

        loadEvents();

        findViewById(R.id.btnSendPush).setOnClickListener(v -> {
            Log.d(TAG, "STEP 1: Send button clicked");

            if (eventTitles.isEmpty()) {
                Log.d(TAG, "STEP 2: Events list empty");
                Toast.makeText(this, "Still loading events...", Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = spinnerEvents.getSelectedItemPosition();
            Log.d(TAG, "STEP 3: Spinner position = " + pos);

            if (pos >= 0) {
                Log.d(TAG, "STEP 4: Calling triggerPush()");
                triggerPush(eventTitles.get(pos), eventIds.get(pos));
            }
        });
    }

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

                    Log.d(TAG, "Events loaded: " + eventTitles.size());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            eventTitles
                    );
                    spinnerEvents.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to load events", e)
                );
    }

    private void triggerPush(String title, String id) {

        Log.d(TAG, "STEP 5: triggerPush() entered");
        Log.d(TAG, "STEP 6: Event title = " + title);
        Log.d(TAG, "STEP 7: Event ID = " + id);

        String url = "https://scaling-trust-ai.onrender.com/send-notification";
        Log.d(TAG, "STEP 8: URL = " + url);

        JSONObject jsonBody = new JSONObject();
        try {
            Log.d(TAG, "STEP 9: Creating JSON body");

            jsonBody.put("title", "New Event: " + title);
            jsonBody.put("body", "Check out the details for " + title);
            jsonBody.put("topic", "all_members");

            Log.d(TAG, "STEP 10: JSON = " + jsonBody.toString());

        } catch (JSONException e) {
            Log.e(TAG, "JSON creation failed", e);
            return;
        }

        Log.d(TAG, "STEP 11: Creating Volley request");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d(TAG, "STEP 14: Volley SUCCESS");
                    Log.d(TAG, "Response: " + response.toString());
                    Toast.makeText(this, "Broadcast Request Sent!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e(TAG, "STEP 14: Volley ERROR", error);

                    if (error.networkResponse != null) {
                        Log.e(TAG, "HTTP Status: " + error.networkResponse.statusCode);
                    } else {
                        Log.e(TAG, "No network response (timeout / DNS / SSL)");
                    }

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

        Log.d(TAG, "STEP 12: Adding request to Volley queue");

        request.setRetryPolicy(
                new com.android.volley.DefaultRetryPolicy(
                        60000,
                        0,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        Volley.newRequestQueue(this).add(request);

        Log.d(TAG, "STEP 13: Request added to queue");
    }
}
