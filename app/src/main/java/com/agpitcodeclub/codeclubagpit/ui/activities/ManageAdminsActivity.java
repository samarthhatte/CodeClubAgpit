package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.WindowCompat;
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

    // ✅ declare listener here
    private ListenerRegistration adminListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admins);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);


        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.rvAdmins);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageAdminAdapter(adminList);
        recyclerView.setAdapter(adapter);

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

                    // ✅ only after verification → load admins
                    loadAdmins(db, uid);
                })
                .addOnFailureListener(e -> finish());
    }

    private void loadAdmins(FirebaseFirestore db, String currentUid) {

        // ✅ store listener
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

                        // ❌ prevent self demotion
                        if (doc.getId().equals(currentUid)) continue;

                        adminList.add(user);
                    }

                    adapter.notifyDataSetChanged();
                });
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
