package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.adapters.EventAdapter;
import com.agpitcodeclub.codeclubagpit.ui.adapters.SliderAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private FirebaseFirestore db;
    private final List<DocumentSnapshot> eventList = new ArrayList<>();

    // Slider variables
    private ViewPager2 viewPagerSlider;
    private SliderAdapter sliderAdapter;
    private final List<String> sliderImages = new ArrayList<>();
    private final List<String> sliderTitles = new ArrayList<>();
    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        db = FirebaseFirestore.getInstance();
        rvEvents = findViewById(R.id.rvEvents);
        viewPagerSlider = findViewById(R.id.viewPagerSlider);

        // Get role from Intent (passed from MainActivity)
        String role = getIntent().getStringExtra("role");
        isAdmin = "admin".equals(role) || "super_admin".equals(role);

        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // 1. Initialize with Static Content
        initStaticSlider();

        // 2. Setup Auto-Slide Logic
        setupAutoSlide();

        // 3. Load Dynamic Content (List + Slider Update)
        loadEventsFromFirestore();
    }

    private void initStaticSlider() {
        sliderImages.add("https://codeclubagpit.vercel.app/events/JS1.png");
        sliderTitles.add("Android Workshop 2026");

        sliderImages.add("https://codeclubagpit.vercel.app/events/launchFY1.png");
        sliderTitles.add("Code Club Meetup");

        sliderAdapter = new SliderAdapter(sliderImages, sliderTitles);
        viewPagerSlider.setAdapter(sliderAdapter);
    }

    private void setupAutoSlide() {
        sliderHandler = new Handler();
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (!sliderImages.isEmpty()) {
                    int next = (viewPagerSlider.getCurrentItem() + 1) % sliderImages.size();
                    viewPagerSlider.setCurrentItem(next, true);
                    sliderHandler.postDelayed(this, 3000);
                }
            }
        };

        // --- Add this block ---
        viewPagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Reset the timer whenever the page changes (manual or auto)
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // Optional: Pause while dragging, resume when idle
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    sliderHandler.removeCallbacks(sliderRunnable);
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }
            }
        });
        // ----------------------

        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void loadEventsFromFirestore() {
        db.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    eventList.addAll(queryDocumentSnapshots.getDocuments());

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        // CHANGED: Use "imageUrl" and "title" to match your DB
                        String imageUrl = doc.getString("imageUrl");
                        String title = doc.getString("title");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Adding to the top of the list
                            sliderImages.add(0, imageUrl);
                            sliderTitles.add(0, title != null ? title : "New Event");
                        }
                    }

                    // Notify adapter that data has changed
                    sliderAdapter.notifyDataSetChanged();

// Pass the isAdmin boolean we retrieved in onCreate
                    EventAdapter adapter = new EventAdapter(eventList, isAdmin);
                    rvEvents.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    public void removeEventFromSlider(String imageUrl) {
        if (imageUrl == null) return;

        int index = sliderImages.indexOf(imageUrl);
        if (index != -1) {
            sliderImages.remove(index);
            sliderTitles.remove(index);
            sliderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderHandler != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }
}