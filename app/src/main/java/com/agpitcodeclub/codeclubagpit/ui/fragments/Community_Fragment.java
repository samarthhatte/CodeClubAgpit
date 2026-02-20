package com.agpitcodeclub.codeclubagpit.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.agpitcodeclub.codeclubagpit.R;

public class Community_Fragment extends Fragment {

    private RecyclerView upcomingEventsRecycler;
    private RecyclerView teamMembersRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // Initialize the Recyclers
        upcomingEventsRecycler = view.findViewById(R.id.upcomingEventsRecyclerView);
        teamMembersRecycler = view.findViewById(R.id.teamMembersRecyclerView);

        // Setup logic for these recyclers will go here

        return view;
    }
}