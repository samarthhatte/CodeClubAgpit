package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.view.WindowCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity {

    private String currentRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            finish();
            return;
        }

        MaterialCardView cardAddEvent = findViewById(R.id.cardAddEvent);
        MaterialCardView cardManageMembers = findViewById(R.id.cardManageMembers);
        MaterialCardView cardNotifications = findViewById(R.id.cardNotifications);
        MaterialCardView cardGallery = findViewById(R.id.cardGallery);
        MaterialCardView cardManageAdmins = findViewById(R.id.cardManageAdmins);

        // 🔐 FETCH ROLE FROM FIRESTORE
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    currentRole = doc.getString("role");
                    Toast.makeText(this, "ROLE = " + currentRole, Toast.LENGTH_LONG).show();
                    if (currentRole == null) {
                        finish();
                        return;
                    }
                    if (!"admin".equals(currentRole) && !"super_admin".equals(currentRole)) {
                        finish();
                        return;
                    }
                    // ✅ SHOW UI only after verification
                    findViewById(R.id.rootAdminLayout).setVisibility(View.VISIBLE);


                    // ❌ Block everyone except admin & super admin
                    if (!"admin".equals(currentRole) && !"super_admin".equals(currentRole)) {
                        finish();
                        return;
                    }

                    // 🔁 ROLE BASED VISIBILITY
                    if ("super_admin".equals(currentRole)) {
                        cardManageAdmins.setVisibility(View.VISIBLE);
                     //   cardManageMembers.setVisibility(View.GONE);
                    } else {
                        cardManageAdmins.setVisibility(View.GONE);
                        cardManageMembers.setVisibility(View.VISIBLE);
                    }

                    // 🔘 CLICK HANDLERS (ROLE SAFE)
                    cardAddEvent.setOnClickListener(v ->
                            startActivity(new Intent(this, AddEventActivity.class))
                    );

                    cardGallery.setOnClickListener(v ->
                            startActivity(new Intent(this, AdminGallery.class))
                    );

                    cardNotifications.setOnClickListener(v ->
                            startActivity(new Intent(this, AdminPushActivity.class))
                    );

                    cardManageMembers.setOnClickListener(v -> {
                        if (!"admin".equals(currentRole) && !"super_admin".equals(currentRole)) return;
                        Intent intent = new Intent(this, MembersActivity.class);
                        intent.putExtra("role", currentRole);
                        startActivity(intent);
                    });

                    cardManageAdmins.setOnClickListener(v -> {
                        if (!"super_admin".equals(currentRole)) return;
                        startActivity(new Intent(this, ManageAdminsActivity.class));
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
                    finish();
                });

        // 🔒 LOGOUT
        findViewById(R.id.btnAdminLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}