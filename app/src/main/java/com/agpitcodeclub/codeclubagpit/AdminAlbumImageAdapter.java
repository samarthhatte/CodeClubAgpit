package com.agpitcodeclub.codeclubagpit;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdminAlbumImageAdapter
        extends RecyclerView.Adapter<AdminAlbumImageAdapter.ViewHolder> {

    private final List<String> list;
    private final OnImageDeleteListener listener;

    public interface OnImageDeleteListener {
        void onDelete(int position);
    }

    public AdminAlbumImageAdapter(
            List<String> list,
            OnImageDeleteListener listener
    ) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String url = list.get(position);

        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imageView);

        // Open full screen
        holder.imageView.setOnClickListener(v -> {
            Intent intent =
                    new Intent(v.getContext(), FullScreenImageActivity.class);
            intent.putExtra("IMAGE_URL", url);
            v.getContext().startActivity(intent);
        });

        // Long press delete
        holder.imageView.setOnLongClickListener(v -> {
            listener.onDelete(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imgAlbum);
        }
    }
}

