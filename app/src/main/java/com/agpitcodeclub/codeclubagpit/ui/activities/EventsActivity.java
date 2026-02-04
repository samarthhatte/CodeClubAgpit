package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.ui.adapters.EventAdapter;
import com.agpitcodeclub.codeclubagpit.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private FirebaseFirestore db;
    private final List<DocumentSnapshot> eventList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ImageSlider imageSlider = findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        rvEvents = findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // Add images for your Code Club (Use your actual Firebase image URLs here)
        slideModels.add(new SlideModel("https://codeclubagpit.vercel.app/events/JS1.png", "Android Workshop 2026", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://lh3.googleusercontent.com/pw/AP1GczM6xtRuWFZkR6UAMmBt6vMRNm-JgXgSkHFrDsWxZd_W39dlIlepltwWF8sqSa6vCaPBpYuv28LcTOyHi4ddHYGjYjMoWaf2bRzOQ41P7vNov-0cxiHyr3LEHEqzUAcffyIlBGRNWVzZah45I15hmIMd=w878-h897-s-no-gm?authuser=0", "AGPIT Hackathon", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://codeclubagpit.vercel.app/events/launchFY1.png", "Code Club Meetup", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);

        loadEventsFromFirestore();
    }

    private void loadEventsFromFirestore() {
        db.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    eventList.addAll(queryDocumentSnapshots.getDocuments());

                    EventAdapter adapter = new EventAdapter(eventList);
                    rvEvents.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

}