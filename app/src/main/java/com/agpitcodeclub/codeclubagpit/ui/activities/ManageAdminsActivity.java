package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.UserModel;
import com.agpitcodeclub.codeclubagpit.ui.adapters.ManageAdminAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ManageAdminsActivity extends AppCompatActivity {

    private final List<UserModel> adminList = new ArrayList<>();
    private ManageAdminAdapter adapter;
    private TextView tvAdminCount;
    private RecyclerView rvAdmins; // Declared
    private View emptyStateContainer;
    private ListenerRegistration adminListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admins);

        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        // Bind Views
        tvAdminCount = findViewById(R.id.tvAdminCount);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        rvAdmins = findViewById(R.id.rvAdmins);

        // Setup RecyclerView
        rvAdmins.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageAdminAdapter(adminList);
        rvAdmins.setAdapter(adapter);

        // Handle System Insets
        View mainView = findViewById(R.id.mainAdminLayout);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            finish();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 🔐 Verify super admin FIRST
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String role = doc.getString("role");
                    if (!"super_admin".equals(role)) {
                        finish();
                        return;
                    }
                    loadAdmins(db, uid);
                })
                .addOnFailureListener(e -> finish());
    }

    private void loadAdmins(FirebaseFirestore db, String currentUid) {
        adminListener = db.collection("users")
                .whereEqualTo("role", "admin")
                .addSnapshotListener((snap, err) -> {
                    if (err != null) {
                        Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snap == null) return;

                    adminList.clear();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        UserModel user = doc.toObject(UserModel.class);
                        if (user == null) continue;

                        user.setId(doc.getId());

                        // ❌ prevent self-view/demotion
                        if (doc.getId().equals(currentUid)) continue;

                        adminList.add(user);
                    }

                    // ✅ Call UI update once AFTER the loop
                    updateUI();
                });
    }

    private void updateUI() {
        int count = adminList.size();
        adapter.notifyDataSetChanged();

        if (count == 0) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            rvAdmins.setVisibility(View.GONE);
            tvAdminCount.setText("ADMINS (0)");
        } else {
            emptyStateContainer.setVisibility(View.GONE);
            rvAdmins.setVisibility(View.VISIBLE);
            tvAdminCount.setText("ADMINS (" + count + ")");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adminListener != null) {
            adminListener.remove();
            adminListener = null;
        }
    }
}