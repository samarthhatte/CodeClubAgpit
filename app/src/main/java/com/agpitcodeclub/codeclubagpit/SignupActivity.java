package com.agpitcodeclub.codeclubagpit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText nameField, emailField, githubField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        if (name.isEmpty() || email.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirestore(name, email, github);
                    } else {
                        Toast.makeText(this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String name, String email, String github) {
        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("github", github);
        userMap.put("role", "student"); // Default role
        userMap.put("skills", Arrays.asList("Java")); // Default skill to start

        db.collection("users").document(uid).set(userMap)
                .addOnSuccessListener(aVoid -> {
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    finish();
                });
    }
}