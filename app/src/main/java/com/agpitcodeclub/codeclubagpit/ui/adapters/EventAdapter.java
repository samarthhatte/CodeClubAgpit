package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventDetailActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<DocumentSnapshot> events;

    public EventAdapter(List<DocumentSnapshot> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = events.get(position);

        // 1. Extract data once
        String title = doc.getString("title");
        String date = doc.getString("date");
        String imageUrl = doc.getString("imageUrl");
        String time = doc.getString("time");
        String location = doc.getString("location");

        // 2. Set Basic Info
        holder.tvTitle.setText(title != null ? title : "Event Name");
        holder.tvDate.setText(date != null ? date : "Date TBA");

        // 3. Set Time (Conditional check in case ID is missing in XML)
        if (holder.tvTime != null) {
            holder.tvTime.setText(time != null ? time : "Time TBA");
        }

        // 4. Set Location
        if (holder.tvLocation != null) {
            holder.tvLocation.setText(location != null ? location : "AGPIT Solapur");
        }

        // 5. Load Image with Glide
        if (holder.ivEventBanner != null && imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.img)
                    .into(holder.ivEventBanner);
        }

        // 6. Navigation
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("eventId", doc.getId());
            v.getContext().startActivity(intent);
        });

        // 7. Entry Animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(400).start();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvTime, tvLocation;
        ImageView ivEventBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);

            // New IDs - make sure these match your item_event.xml
            tvTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            ivEventBanner = itemView.findViewById(R.id.ivEventBanner);
        }
    }
}