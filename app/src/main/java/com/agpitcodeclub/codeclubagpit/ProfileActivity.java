package com.agpitcodeclub.codeclubagpit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.ImageButton;


import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView ivProfilePic;
    private TextView tvUserName;
    private ChipGroup cgSkills;
    private EditText etGithub, etLinkedIn, etEmail, etPortfolio;
    private ImageButton btnEditGithub, btnEditLinkedIn, btnEditEmail, btnEditPortfolio;
    private EditText currentEditingField = null;



    private FirebaseFirestore db;
    private String currentUserId;

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Cloudinary Init (ONLY ONCE)


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvUserName = findViewById(R.id.tvUserName);
        cgSkills = findViewById(R.id.cgSkills);
        Button btnAddSkill = findViewById(R.id.btnAddSkill);
        FloatingActionButton btnEditPic = findViewById(R.id.btnEditPic);
        etGithub = findViewById(R.id.etGithub);
        etLinkedIn = findViewById(R.id.etLinkedIn);
        etEmail = findViewById(R.id.etEmail);
        etPortfolio = findViewById(R.id.etPortfolio);

        btnEditGithub = findViewById(R.id.btnEditGithub);
        btnEditLinkedIn = findViewById(R.id.btnEditLinkedIn);
        btnEditEmail = findViewById(R.id.btnEditEmail);
        btnEditPortfolio = findViewById(R.id.btnEditPortfolio);

// Pencil click handlers
        btnEditGithub.setOnClickListener(v -> enableEdit(etGithub, "github"));
        btnEditLinkedIn.setOnClickListener(v -> enableEdit(etLinkedIn, "linkedin"));
        btnEditEmail.setOnClickListener(v -> enableEdit(etEmail, "email"));
        btnEditPortfolio.setOnClickListener(v -> enableEdit(etPortfolio, "portfolio"));


        loadUserProfile();

        btnEditPic.setOnClickListener(v -> openGallery());
        btnAddSkill.setOnClickListener(v -> showAddSkillDialog());
    }

    private void loadUserProfile() {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        // Safety check for name
                        String name = doc.getString("name");
                        tvUserName.setText(name != null ? name : "AGPIT Member");

                        etGithub.setText(doc.getString("github"));
                        etLinkedIn.setText(doc.getString("linkedin"));
                        etEmail.setText(doc.getString("email"));
                        etPortfolio.setText(doc.getString("portfolio"));

// Load skills
// Load skills safely
                        List<String> skills = (List<String>) doc.get("skills");
                        if (skills != null) {
                            cgSkills.removeAllViews(); // Clear existing chips to avoid duplicates
                            for (String skill : skills) {
                                addSkillChip(skill);
                            }
                        }


                        String photoUrl = doc.getString("profilePic");
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(this).load(photoUrl).circleCrop().into(ivProfilePic);
                        }

                        // ... skills logic
                    } else {
                        // This handles the "Profile not found" scenario
                        Log.d("Firestore", "User document missing for UID: " + currentUserId);
                        tvUserName.setText("New Member");
                        Toast.makeText(this, "Welcome! Please set up your profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImageToCloudinary(data.getData());
        }
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        MediaManager.get().upload(imageUri)
                .unsigned("profile_upload") // your preset
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                        Log.d("CLOUDINARY", "Upload started");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d("CLOUDINARY", "Progress: " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d("CLOUDINARY", "Upload success: " + imageUrl);

                        // Save URL to Firestore
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(currentUserId)
                                .update("profilePic", imageUrl);

                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .circleCrop()
                                .into(ivProfilePic);

                        Toast.makeText(ProfileActivity.this,
                                "Profile picture updated",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("CLOUDINARY", "Upload error: " + error.getDescription());
                        Toast.makeText(ProfileActivity.this,
                                "Upload failed: " + error.getDescription(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.w("CLOUDINARY", "Upload rescheduled");
                    }
                })
                .dispatch();
    }

        private void showAddSkillDialog() {
        EditText etSkill = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Add Skill")
                .setView(etSkill)
                .setPositiveButton("Add", (dialog, which) -> {
                    String skill = etSkill.getText().toString().trim();
                    if (!skill.isEmpty()) {
                        updateSkillsInFirestore(skill);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateSkillsInFirestore(String skill) {
        db.collection("users").document(currentUserId)
                .update("skills", FieldValue.arrayUnion(skill))
                .addOnSuccessListener(v -> {
                    addSkillChip(skill);
                    Toast.makeText(this, "Skill Added", Toast.LENGTH_SHORT).show();
                });
    }

    private void addSkillChip(String skill) {
        Chip chip = new Chip(this);
        chip.setText(skill);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v ->
                db.collection("users").document(currentUserId)
                        .update("skills", FieldValue.arrayRemove(skill))
                        .addOnSuccessListener(aVoid -> cgSkills.removeView(chip))
        );
        cgSkills.addView(chip);
    }

    private void enableEdit(EditText editText, String fieldName) {

        // If clicking same field again → SAVE
        if (currentEditingField == editText) {
            saveField(editText, fieldName);
            editText.setEnabled(false);
            currentEditingField = null;
            return;
        }

        // 🔥 SAVE previous field BEFORE switching
        if (currentEditingField != null) {
            String previousFieldName = (String) currentEditingField.getTag();
            saveField(currentEditingField, previousFieldName);
            currentEditingField.setEnabled(false);
        }

        // EDIT MODE
        editText.setEnabled(true);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());

        // 🔐 Store field name safely
        editText.setTag(fieldName);
        currentEditingField = editText;

        Toast.makeText(this, "Editing enabled", Toast.LENGTH_SHORT).show();
    }



    private void saveField(EditText editText, String fieldName) {
        String value = editText.getText().toString().trim();

        editText.setEnabled(false);

        if (value.isEmpty()) return;

        Map<String, Object> data = new java.util.HashMap<>();
        data.put(fieldName, value);

        db.collection("users")
                .document(currentUserId)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
                );
    }



}
