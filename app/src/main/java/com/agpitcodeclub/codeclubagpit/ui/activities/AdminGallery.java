package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.AlbumModel;
import com.agpitcodeclub.codeclubagpit.ui.adapters.AdminAlbumAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminGallery extends AppCompatActivity {

    RecyclerView rvAdminAlbums;
    List<AlbumModel> albumList;
    AdminAlbumAdapter adapter;
    MaterialButton btnCreateAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_gallery);

        btnCreateAlbum = findViewById(R.id.btnCreateAlbum);
        rvAdminAlbums = findViewById(R.id.rvAdminAlbums);

        rvAdminAlbums.setLayoutManager(new LinearLayoutManager(this));

        albumList = new ArrayList<>();
        adapter = new AdminAlbumAdapter(this, albumList);

        rvAdminAlbums.setAdapter(adapter);

        btnCreateAlbum.setOnClickListener(v ->
                startActivity(new Intent(this, CreateAlbumActivity.class)));

        loadAlbums();
    }

    private void loadAlbums() {
        FirebaseFirestore.getInstance()
                .collection("gallery")
                .get()
                .addOnSuccessListener(query -> {
                    albumList.clear();
                    for (DocumentSnapshot doc : query) {
                        albumList.add(new AlbumModel(
                                doc.getId(),
                                doc.getString("albumName"),
                                doc.getString("coverImage")
                        ));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}