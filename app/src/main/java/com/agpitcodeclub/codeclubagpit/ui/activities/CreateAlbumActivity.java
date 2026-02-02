package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agpitcodeclub.codeclubagpit.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAlbumActivity extends AppCompatActivity {

    TextInputEditText etAlbumName;
    MaterialButton btnSaveAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        etAlbumName = findViewById(R.id.etAlbumName);
        btnSaveAlbum = findViewById(R.id.btnSaveAlbum);

        btnSaveAlbum.setOnClickListener(v -> createAlbum());
    }

    private void createAlbum() {
        String albumName = Objects.requireNonNull(etAlbumName.getText()).toString().trim();

        if (TextUtils.isEmpty(albumName)) {
            etAlbumName.setError("Album name required");
            return;
        }

        Map<String, Object> album = new HashMap<>();
        album.put("albumName", albumName);
        album.put("coverImage", ""); // will be added later
        album.put("createdAt", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("gallery")
                .add(album)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Album created", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to create album", Toast.LENGTH_SHORT).show()
                );
    }
}
