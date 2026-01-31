package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        // Inside LoginActivity onCreate
        TextView tvGoToSignup = findViewById(R.id.tvSignup);
        tvGoToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        btnLogin.setOnClickListener(v -> loginUser());


    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkUserRole();
                    } else {
                        Toast.makeText(LoginActivity.this, "Auth Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserRole() {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this,
                                "Profile not found. Please register.",
                                Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        return;
                    }

                    String role = documentSnapshot.getString("role");

                    if ("admin".equals(role) || "super_admin".equals(role)) {
                        Toast.makeText(this,
                                "Login successful (" + role + ")",
                                Toast.LENGTH_SHORT).show();

                        startActivity(
                                new Intent(LoginActivity.this,
                                        AdminDashboardActivity.class)
                        );

                    } else {
                        Toast.makeText(this,
                                "Login successful",
                                Toast.LENGTH_SHORT).show();

                        startActivity(
                                new Intent(LoginActivity.this,
                                        MainActivity.class)
                        );
                    }

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Database Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, skip login screen and check role
            checkUserRole();
        }
    }



}