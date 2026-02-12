package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.view.WindowCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.ui.adapters.AlbumAdapter;
import com.agpitcodeclub.codeclubagpit.model.AlbumModel;
import com.agpitcodeclub.codeclubagpit.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerGallery;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private AlbumAdapter adapter;
    private final List<AlbumModel> albumList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);


        recyclerGallery = findViewById(R.id.recyclerGallery);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerGallery.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AlbumAdapter(this, albumList);
        recyclerGallery.setAdapter(adapter);

        fetchAlbums();
    }

    private void fetchAlbums() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gallery")
                .orderBy("createdAt")
                .get()
                .addOnSuccessListener(snapshot -> {

                    albumList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {

                        AlbumModel album = doc.toObject(AlbumModel.class);

                        String albumId = doc.getId();

                        albumList.add(new AlbumModel(
                                albumId,
                                album.getAlbumName(),
                                album.getCoverImage()
                        ));

                        // 🔥 THIS WAS MISSING
                        fixCoverIfNeeded(albumId, album.getCoverImage());
                    }

                    progressBar.setVisibility(View.GONE);

                    if (albumList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Failed to load albums");
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchAlbums(); // reload albums every time screen opens
    }

    private void fixCoverIfNeeded(String albumId, String coverImage) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gallery")
                .document(albumId)
                .collection("images")
                .orderBy("uploadedAt")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (!query.isEmpty()) {
                        String firstImage =
                                query.getDocuments().get(0).getString("imageUrl");

                        if (firstImage != null && !firstImage.equals(coverImage)) {
                            db.collection("gallery")
                                    .document(albumId)
                                    .update("coverImage", firstImage)
                                    .addOnSuccessListener(v -> {
                                        // 🔥 REFRESH UI AFTER FIX
                                        fetchAlbums();
                                    });
                        }

                    } else {
                        db.collection("gallery")
                                .document(albumId)
                                .update("coverImage", null)
                                .addOnSuccessListener(v -> fetchAlbums());
                    }
                });
    }



}

