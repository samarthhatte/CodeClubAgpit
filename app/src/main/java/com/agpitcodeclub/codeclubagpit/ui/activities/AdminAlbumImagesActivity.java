package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.util.Map;
import androidx.core.view.WindowCompat;

import com.agpitcodeclub.codeclubagpit.ui.adapters.AdminAlbumImageAdapter;
import com.agpitcodeclub.codeclubagpit.utils.FileUtils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AdminAlbumImagesActivity extends AppCompatActivity {

    RecyclerView rvAlbumImages;
    private static final int PICK_IMAGES = 101;
    FloatingActionButton fabAddImage;

    List<String> imageList; // image URLs
    AdminAlbumImageAdapter adapter;

    String albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_album_images);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        // 1️⃣ Read albumId FIRST
        albumId = getIntent().getStringExtra("ALBUM_ID");

        Log.d("ALBUM_DEBUG", "albumId = " + albumId);

        if (albumId == null || albumId.trim().isEmpty()) {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2️⃣ Init views
        rvAlbumImages = findViewById(R.id.rvAlbumImages);
        fabAddImage = findViewById(R.id.fabAddImage);

        rvAlbumImages.setLayoutManager(new GridLayoutManager(this, 3));

        // 3️⃣ Init list FIRST
        imageList = new ArrayList<>();

        // 4️⃣ Init adapter ONCE with delete listener
        adapter = new AdminAlbumImageAdapter(imageList, this::showDeleteDialog);
        rvAlbumImages.setAdapter(adapter);

        // 5️⃣ Load images from Firestore
        loadImagesFromFirestore();

        // 6️⃣ Add image button
        fabAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGES);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    uploadImageToCloudinary(uri);
                }
            } else if (data.getData() != null) {
                uploadImageToCloudinary(data.getData());
            }


            adapter.notifyDataSetChanged();
        }
    }

    private void uploadImageToCloudinary(Uri imageUri) {

        String cloudName = "dc67ajheo"; // 🔴 replace
        String uploadPreset = "gallery_upload";

        OkHttpClient client = new OkHttpClient();

        String path = FileUtils.getPath(this, imageUri);
        if (path == null) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Invalid image", Toast.LENGTH_SHORT).show());
            return;
        }

        File file = new File(path);


        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(file, MediaType.parse("image/*"))
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .addFormDataPart("folder", "gallery/" + albumId)
                .build();

        Request request = new Request.Builder()
                .url("https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AdminAlbumImagesActivity.this,
                                "Upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {

                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    String imageUrl = json.getString("secure_url");
                    String publicId = json.getString("public_id");

                    saveImageToFirestore(imageUrl, publicId);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void showDeleteDialog(int position) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (d, w) -> deleteImage(position))
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void deleteImage(int position) {

        String deletedImageUrl = imageList.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gallery")
                .document(albumId)
                .collection("images")
                .whereEqualTo("imageUrl", deletedImageUrl)
                .get()
                .addOnSuccessListener(query -> {

                    for (DocumentSnapshot doc : query) {

                        String publicId = doc.getString("publicId");

                        if (publicId != null && !publicId.isEmpty()) {
                            deleteFromCloudinary(publicId);
                        }

                        doc.getReference().delete();
                    }

                    // ✅ Update local list
                    imageList.remove(position);
                    adapter.notifyItemRemoved(position);

                    // ✅ FIX COVER IMAGE AFTER DELETE
                    updateAlbumCoverAfterDelete(deletedImageUrl);
                });
    }
    private void updateAlbumCoverAfterDelete(String deletedImageUrl) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gallery")
                .document(albumId)
                .get()
                .addOnSuccessListener(doc -> {

                    String currentCover = doc.getString("coverImage");

                    // Only act if deleted image WAS the cover
                    if (currentCover != null && currentCover.equals(deletedImageUrl)) {

                        if (!imageList.isEmpty()) {
                            // ✅ Set next image as cover
                            db.collection("gallery")
                                    .document(albumId)
                                    .update("coverImage", imageList.get(0));
                        } else {
                            // ✅ No images left → remove cover
                            db.collection("gallery")
                                    .document(albumId)
                                    .update("coverImage", null);
                        }
                    }
                });
    }

    private void deleteFromCloudinary(String publicId) {

        if (publicId == null || publicId.trim().isEmpty()) {
            Log.e("DELETE", "publicId is null, skipping Cloudinary delete");
            return;
        }

        String cloudName = "dc67ajheo";
        String apiKey = "YOUR_API_KEY";
        String apiSecret = "YOUR_API_SECRET";

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        String signature = Utils.sha1(
                "public_id=" + publicId +
                        "&timestamp=" + timestamp +
                        apiSecret
        );

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("public_id", publicId)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("timestamp", timestamp)
                .addFormDataPart("signature", signature)
                .build();

        Request request = new Request.Builder()
                .url("https://api.cloudinary.com/v1_1/" + cloudName + "/image/destroy")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) {}
        });
    }


    private void saveImageToFirestore(String imageUrl, String publicId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put("imageUrl", imageUrl);
        map.put("publicId", publicId);
        map.put("uploadedAt", System.currentTimeMillis());

        db.collection("gallery")
                .document(albumId)
                .collection("images")
                .add(map)
                .addOnSuccessListener(doc -> runOnUiThread(() -> {
                    imageList.add(imageUrl);
                    adapter.notifyDataSetChanged();
                    setAlbumCoverIfEmpty(imageUrl);
                }));
    }

    private void setAlbumCoverIfEmpty(String imageUrl) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gallery")
                .document(albumId)
                .get()
                .addOnSuccessListener(doc -> {
                    String cover = doc.getString("coverImage");
                    if (cover == null || cover.isEmpty()) {
                        db.collection("gallery")
                                .document(albumId)
                                .update("coverImage", imageUrl);
                    }
                });
    }

    private void loadImagesFromFirestore() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gallery")
                .document(albumId)
                .collection("images")
                .orderBy("uploadedAt")
                .get()
                .addOnSuccessListener(query -> {

                    imageList.clear();

                    for (DocumentSnapshot doc : query) {
                        String url = doc.getString("imageUrl");
                        if (url != null && !url.isEmpty()) {
                            imageList.add(url);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // ✅ SELF-HEAL COVER IMAGE
                    db.collection("gallery")
                            .document(albumId)
                            .get()
                            .addOnSuccessListener(albumDoc -> {

                                String cover = albumDoc.getString("coverImage");

                                if ((cover == null || cover.isEmpty())
                                        && !imageList.isEmpty()) {

                                    // Set first image as cover
                                    db.collection("gallery")
                                            .document(albumId)
                                            .update("coverImage", imageList.get(0));
                                }

                                // No images → ensure cover is null
                                if (imageList.isEmpty()) {
                                    db.collection("gallery")
                                            .document(albumId)
                                            .update("coverImage", null);
                                }
                            });
                });
    }



}
