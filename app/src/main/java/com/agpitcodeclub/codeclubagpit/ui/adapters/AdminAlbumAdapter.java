package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.AlbumModel;
import com.agpitcodeclub.codeclubagpit.ui.activities.AdminAlbumImagesActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminAlbumAdapter
        extends RecyclerView.Adapter<AdminAlbumAdapter.ViewHolder> {

    private final Context context;
    private final List<AlbumModel> list;

    public AdminAlbumAdapter(Context context, List<AlbumModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_album, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AlbumModel album = list.get(position);
        holder.tvAlbumName.setText(album.getAlbumName());

        // 🔥 ALWAYS derive cover from images (admin side)
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
                                query.getDocuments()
                                        .get(0)
                                        .getString("imageUrl");

                        Glide.with(context)
                                .load(firstImage)
                                .placeholder(R.drawable.ic_gallery_placeholder)
                                .error(R.drawable.ic_gallery_placeholder)
                                .centerCrop()
                                .into(holder.imgAlbum);
                    } else {
                        holder.imgAlbum.setImageResource(
                                R.drawable.ic_gallery_placeholder
                        );
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent =
                    new Intent(context, AdminAlbumImagesActivity.class);
            intent.putExtra("ALBUM_ID", album.getId());
            intent.putExtra("ALBUM_NAME", album.getAlbumName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAlbum;
        TextView tvAlbumName;

        ViewHolder(@NonNull View v) {
            super(v);
            imgAlbum = v.findViewById(R.id.imgAlbum);
            tvAlbumName = v.findViewById(R.id.tvAlbumName);
        }
    }
}
