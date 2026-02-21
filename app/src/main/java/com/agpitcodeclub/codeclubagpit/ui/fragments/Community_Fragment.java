package com.agpitcodeclub.codeclubagpit.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.adapters.CommunityEventAdapter;
import com.agpitcodeclub.codeclubagpit.ui.adapters.EventAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Community_Fragment extends Fragment {

    private RecyclerView upcomingEventsRecycler;
    private RecyclerView teamMembersRecycler;

    private FirebaseFirestore db;
    private List<DocumentSnapshot> upcomingEventsList = new ArrayList<>();
    private EventAdapter eventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        db = FirebaseFirestore.getInstance();

        // 1. Initialize the Recyclers
        upcomingEventsRecycler = view.findViewById(R.id.upcomingEventsRecyclerView);
        teamMembersRecycler = view.findViewById(R.id.teamMembersRecyclerView);

        // 2. Setup Layout Manager (Horizontal for Upcoming Events)
        upcomingEventsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // IMPORTANT: Smooth scrolling fix for NestedScrollView
        upcomingEventsRecycler.setNestedScrollingEnabled(false);
        teamMembersRecycler.setNestedScrollingEnabled(false);

        // 3. Load Data
        loadUpcomingEvents();

        return view;
    }

// Inside loadUpcomingEvents() in Community_Fragment.java

    private void loadUpcomingEvents() {
        db.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    upcomingEventsList.clear();
                    upcomingEventsList.addAll(queryDocumentSnapshots.getDocuments());

                    // Use the NEW CommunityEventAdapter
                    CommunityEventAdapter adapter = new CommunityEventAdapter(upcomingEventsList);
                    upcomingEventsRecycler.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    if(getActivity() != null)
                        Toast.makeText(getActivity(), "Failed to load events", Toast.LENGTH_SHORT).show();
                });
    }
}