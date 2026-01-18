package com.agpitcodeclub.codeclubagpit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
        // ITS THE DASHBOARD ACTIVITY FOR NORMAL USERS
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvWelcome = findViewById(R.id.tvWelcome);

        // Personalize the dashboard
        loadUserName();

        // Initialize Cards
        MaterialCardView cardMembers = findViewById(R.id.cardMembers);
        MaterialCardView cardAlumni = findViewById(R.id.cardAlumni);
        MaterialCardView cardEvents = findViewById(R.id.cardEvents);
        MaterialCardView cardProfile = findViewById(R.id.cardProfile);

        // Navigation listeners
        cardMembers.setOnClickListener(v -> startActivity(new Intent(this, MembersActivity.class)));

        cardAlumni.setOnClickListener(v -> {
            // We can reuse MembersActivity but filter for alumni later
            Intent intent = new Intent(this, MembersActivity.class);
            intent.putExtra("type", "alumni");
            startActivity(intent);
        });

        cardProfile.setOnClickListener(v -> {
            // ProfileActivity (which we'll create next)
            Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserName() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String name = doc.getString("name");
                tvWelcome.setText("Hello, " + name + "!");
            }
        });
    }
}