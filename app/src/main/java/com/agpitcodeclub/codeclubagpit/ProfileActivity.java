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

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView ivProfilePic;
    private TextView tvUserName;
    private ChipGroup cgSkills;
    private Button btnAddSkill;
    private FloatingActionButton btnEditPic;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Cloudinary Init (ONLY ONCE)


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvUserName = findViewById(R.id.tvUserName);
        cgSkills = findViewById(R.id.cgSkills);
        btnAddSkill = findViewById(R.id.btnAddSkill);
        btnEditPic = findViewById(R.id.btnEditPic);

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
}
