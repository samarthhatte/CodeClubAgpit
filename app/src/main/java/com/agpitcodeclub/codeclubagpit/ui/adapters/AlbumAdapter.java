package com.agpitcodeclub.codeclubagpit.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.AlbumModel;
import com.agpitcodeclub.codeclubagpit.ui.activities.AlbumImagesActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private final Context context;
    private final List<AlbumModel> albumList;

    public AlbumAdapter(Context context, List<AlbumModel> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {

        AlbumModel album = albumList.get(position);
        holder.tvAlbumName.setText(album.getAlbumName());

        // Always derive cover from images
        FirebaseFirestore.getInstance()
                .collection("gallery")
                .document(album.getId())
                .collection("images")
                .orderBy("uploadedAt")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (!query.isEmpty()) {
                        String firstImage =
                                query.getDocuments().get(0).getString("imageUrl");

                        Glide.with(context)
                                .load(firstImage)
                                .placeholder(R.drawable.ic_gallery_placeholder)
                                .error(R.drawable.ic_gallery_placeholder)
                                .centerCrop()
                                .into(holder.imgCover);
                    } else {
                        // No images at all
                        holder.imgCover.setImageResource(
                                R.drawable.ic_gallery_placeholder
                        );
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlbumImagesActivity.class);
            intent.putExtra("ALBUM_ID", album.getId());
            intent.putExtra("ALBUM_NAME", album.getAlbumName());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvAlbumName;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
        }
    }
}
