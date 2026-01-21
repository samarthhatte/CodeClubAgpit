package com.agpitcodeclub.codeclubagpit;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AlbumImagesActivity extends AppCompatActivity {

    private RecyclerView rvImages;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private List<String> imageList;
    private UserAlbumImageAdapter adapter;

    private String albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_images);

        albumId = getIntent().getStringExtra("ALBUM_ID");

        if (albumId == null) {
            finish();
            return;
        }

        rvImages = findViewById(R.id.rvAlbumImages);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvImages.setLayoutManager(new GridLayoutManager(this, 3));

        imageList = new ArrayList<>();
        adapter = new UserAlbumImageAdapter(this, imageList);
        rvImages.setAdapter(adapter);

        loadImagesFromFirestore();
    }

    private void loadImagesFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        FirebaseFirestore.getInstance()
                .collection("gallery")
                .document(albumId)
                .collection("images")
                .orderBy("uploadedAt")
                .get()
                .addOnSuccessListener(query -> {

                    imageList.clear();

                    for (DocumentSnapshot doc : query) {
                        String url = doc.getString("imageUrl");
                        if (url != null) imageList.add(url);
                    }

                    progressBar.setVisibility(View.GONE);

                    if (imageList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                });
    }
}
