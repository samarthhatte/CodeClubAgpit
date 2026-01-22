package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etDesc;
    private ImageView imgEvent;
    private Button btnSelectImage, btnSubmit;
    private FirebaseFirestore db;

    private Uri imageUri = null; // OPTIONAL image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etEventTitle);
        etDate = findViewById(R.id.etEventDate);
        etDesc = findViewById(R.id.etEventDesc);
        imgEvent = findViewById(R.id.imgEvent);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmit = findViewById(R.id.btnSubmitEvent);

        btnSelectImage.setOnClickListener(v ->
                imagePicker.launch("image/*")
        );

        btnSubmit.setOnClickListener(v -> submitEvent());
    }

    // System image picker (NO permission needed)
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            imgEvent.setImageURI(uri);
                        }
                    }
            );

    private void submitEvent() {

        String title = etTitle.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Title and Date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Image is OPTIONAL
        if (imageUri != null) {
            uploadImageAndSaveEvent(title, date, desc);
        } else {
            saveEvent(title, date, desc, null);
        }
    }

    // Upload image to Cloudinary ONLY if selected
    private void uploadImageAndSaveEvent(String title, String date, String desc) {

        MediaManager.get().upload(imageUri)
                .unsigned("event_upload")
                .option("folder", "events")
                .option("quality", "auto")
                .option("fetch_format", "auto")
                .callback(new UploadCallback() {

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = resultData.get("secure_url").toString();
                        saveEvent(title, date, desc, imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AddEventActivity.this,
                                "Upload failed: " + error.getDescription(),
                                Toast.LENGTH_LONG).show();

                        // Fallback: save without image
                        saveEvent(title, date, desc, null);
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    // Save event to Firestore (with OR without image)
    private void saveEvent(String title, String date, String desc, String imageUrl) {

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
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
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
