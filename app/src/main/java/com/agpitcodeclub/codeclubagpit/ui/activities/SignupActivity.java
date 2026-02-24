package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.view.WindowCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText nameField, emailField, githubField, passwordField;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String GITHUB_PATTERN = "^(https?:\\/\\/)?(www\\.)?github\\.com\\/[A-Za-z0-9_-]+\\/?$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameField = findViewById(R.id.etName);
        emailField = findViewById(R.id.etEmail);
        githubField = findViewById(R.id.etGithub);
        passwordField = findViewById(R.id.etPassword);

        findViewById(R.id.btnRegister).setOnClickListener(v -> performSignup());
    }

    private void performSignup() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String github = githubField.getText().toString().trim();
        String password = passwordField.getText().toString();

        // 1. Basic Name Validation
        if (name.isEmpty()) {
            nameField.setError("Name is required");
            return;
        }

        // 2. Email RegEx Validation
        if (!email.matches(EMAIL_PATTERN)) {
            emailField.setError("Enter a valid email address");
            return;
        }

        // 3. GitHub RegEx Validation
//        if (!github.isEmpty() && !github.matches(GITHUB_PATTERN)) {
//            githubField.setError("Enter a valid GitHub profile URL");
//            return;
//        }

        // 4. Password Validation
        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            return;
        }

        // If all checks pass, proceed to Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirestore(name, email, github);
                    } else {
                        Toast.makeText(this, "Auth Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String name, String email, String github) {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("github", github);
        userMap.put("skills", Collections.singletonList("Java"));

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        userMap.put("role", "student");
                    }

                    db.collection("users")
                            .document(uid)
                            .set(userMap, SetOptions.merge())
                            .addOnSuccessListener(unused -> {
                                startActivity(new Intent(this, HomePage.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                });
    }

}