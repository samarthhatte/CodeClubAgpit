package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.ui.adapters.MemberAdapter;
import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.UserModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MembersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<UserModel> memberList;
    private MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        db = FirebaseFirestore.getInstance();
        RecyclerView rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));

        memberList = new ArrayList<>();
        adapter = new MemberAdapter(memberList, isAdmin);
        rvMembers.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);

// 1️⃣ Attach listener FIRST
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fetchMembers(tab.getPosition() == 0 ? "student" : "alumni");
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

// 2️⃣ Select tab ONCE (this triggers fetchMembers automatically)
        int openTabIndex = getIntent().getIntExtra("OPEN_TAB_INDEX", 0);
        TabLayout.Tab tab = tabLayout.getTabAt(openTabIndex);
        if (tab != null) {
            tab.select();
            fetchMembers(openTabIndex == 0 ? "student" : "alumni");
        }
    }


    private void fetchMembers(String role) {
        db.collection("users")
                .whereEqualTo("role", role)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        memberList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            memberList.add(doc.toObject(UserModel.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}