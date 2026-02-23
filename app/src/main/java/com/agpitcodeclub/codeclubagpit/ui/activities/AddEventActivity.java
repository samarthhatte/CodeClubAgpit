package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.view.WindowCompat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddEventActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etTime, etLocation, etDesc;
    private ImageView imgEvent;
    private Button btnSelectImage, btnSubmit;
    private FirebaseFirestore db;

    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        EdgeToEdge.enable(this);
        View rootLayout = findViewById(R.id.eventScrollView); // Add this ID to your ScrollView

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to the root view so it doesn't overlap status or nav bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        db = FirebaseFirestore.getInstance();

        // Bind new fields
        etTitle = findViewById(R.id.etEventTitle);
        etDate = findViewById(R.id.etEventDate);
        etTime = findViewById(R.id.etEventTime);
        etLocation = findViewById(R.id.etEventLocation);
        etDesc = findViewById(R.id.etEventDesc);

        imgEvent = findViewById(R.id.imgEvent);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmit = findViewById(R.id.btnSubmitEvent);

        // Date Picker Dialog
        etDate.setOnClickListener(v -> showDatePicker());

        // Time Picker Dialog
        etTime.setOnClickListener(v -> showTimePicker());

        btnSelectImage.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnSubmit.setOnClickListener(v -> submitEvent());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            etDate.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imgEvent.setImageURI(uri);
                }
            });

    private void submitEvent() {
        String title = etTitle.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Title, Date, and Place are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageAndSaveEvent(title, date, time, location, desc);
        } else {
            saveEvent(title, date, time, location, desc, null);
        }
    }

    private void uploadImageAndSaveEvent(String title, String date, String time, String location, String desc) {
        MediaManager.get().upload(imageUri)
                .unsigned("event_upload")
                .option("folder", "events")
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = Objects.requireNonNull(resultData.get("secure_url")).toString();
                        saveEvent(title, date, time, location, desc, imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AddEventActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                        saveEvent(title, date, time, location, desc, null);
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void saveEvent(String title, String date, String time, String location, String desc, String imageUrl) {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
        event.put("time", time);
        event.put("location", location);
        event.put("description", desc);
        event.put("timestamp", FieldValue.serverTimestamp());

        if (imageUrl != null) {
            event.put("imageUrl", imageUrl);
        }

        db.collection("events")
                .add(event)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Event Posted Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}