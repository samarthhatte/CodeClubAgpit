package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.WindowCompat;
import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.UserModel;
import com.agpitcodeclub.codeclubagpit.ui.adapters.MemberAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.ListenerRegistration;



public class MembersActivity extends AppCompatActivity {

    private ListenerRegistration currentListener;
    private FirebaseFirestore db;
    private List<UserModel> memberList;
    private MemberAdapter adapter;
    private TabLayout tabLayout;

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_MEMBER = "member";
    public static final String ROLE_ALUMNI = "alumni";

    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        EdgeToEdge.enable(this);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        // 🔐 Auth safety
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        currentUid = FirebaseAuth.getInstance().getUid();

        String role = getIntent().getStringExtra("role");
        boolean isAdmin = "admin".equals(role) || "super_admin".equals(role);
        boolean isSuperAdmin = "super_admin".equals(role);

        db = FirebaseFirestore.getInstance();

        RecyclerView rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));

        memberList = new ArrayList<>();
        adapter = new MemberAdapter(memberList, isAdmin, isSuperAdmin, currentUid);
        rvMembers.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: fetchMembers(ROLE_STUDENT); break;
                    case 1: fetchMembers(ROLE_MEMBER); break;
                    case 2: fetchMembers(ROLE_ALUMNI); break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });

        // Default tab
        TabLayout.Tab defaultTab = tabLayout.getTabAt(0);
        if (defaultTab != null) defaultTab.select();

        View mainView = findViewById(R.id.membersLayout);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void switchToRoleTab(String role) {
        int tabIndex = 0;
        if (ROLE_MEMBER.equals(role)) tabIndex = 1;
        else if (ROLE_ALUMNI.equals(role)) tabIndex = 2;

        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) tab.select();
    }

    private void fetchMembers(String role) {

        if (currentListener != null) {
            currentListener.remove();
            currentListener = null;
        }

        // 🟢 REMOVED .whereNotEqualTo("role", "super_admin")
        // Firestore won't let you filter equality and inequality on the same field easily
        currentListener = db.collection("users")
                .whereEqualTo("role", role)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) return;

                    memberList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        UserModel user = doc.toObject(UserModel.class);
                        if (user != null) {
                            user.setId(doc.getId());

                            // 🟢 FILTER LOCALLY: Only skip Super Admins
                            // This allows "admin" to be visible if they are in this category
                            if ("super_admin".equals(user.getRole())) {
                                continue;
                            }

                            memberList.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentListener != null) {
            currentListener.remove();
            currentListener = null;
        }
    }
}
