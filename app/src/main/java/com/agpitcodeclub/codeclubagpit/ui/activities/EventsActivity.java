package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.WindowCompat;
import com.agpitcodeclub.codeclubagpit.ui.adapters.EventAdapter;
import com.agpitcodeclub.codeclubagpit.R;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Handler;
import com.agpitcodeclub.codeclubagpit.ui.adapters.SliderAdapter;
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

    private Handler sliderHandler;
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);




        db = FirebaseFirestore.getInstance();
        rvEvents = findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // Add images for your Code Club (Use your actual Firebase image URLs here)
        ViewPager2 viewPagerSlider = findViewById(R.id.viewPagerSlider);

        List<String> images = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        images.add("https://codeclubagpit.vercel.app/events/JS1.png");
        titles.add("Android Workshop 2026");

        images.add("https://lh3.googleusercontent.com/pw/AP1GczM6xtRuWFZkR6UAMmBt6vMRNm-JgXgSkHFrDsWxZd_W39dlIlepltwWF8sqSa6vCaPBpYuv28LcTOyHi4ddHYGjYjMoWaf2bRzOQ41P7vNov-0cxiHyr3LEHEqzUAcffyIlBGRNWVzZah45I15hmIMd=w878-h897-s-no-gm?authuser=0");
        titles.add("AGPIT Hackathon");

        images.add("https://codeclubagpit.vercel.app/events/launchFY1.png");
        titles.add("Code Club Meetup");

        SliderAdapter sliderAdapter = new SliderAdapter(images, titles);
        viewPagerSlider.setAdapter(sliderAdapter);

        sliderHandler = new Handler();

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int next = (viewPagerSlider.getCurrentItem() + 1) % images.size();
                viewPagerSlider.setCurrentItem(next, true);
                sliderHandler.postDelayed(this, 3000);
            }
        };

        sliderHandler.postDelayed(sliderRunnable, 3000);



        loadEventsFromFirestore();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
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