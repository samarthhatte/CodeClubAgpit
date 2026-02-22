package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventDetailActivity;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventsActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<DocumentSnapshot> events;
    private final boolean isAdmin; // Added admin flag

    // Updated constructor
    public EventAdapter(List<DocumentSnapshot> events, boolean isAdmin) {
        this.events = events;
        this.isAdmin = isAdmin;
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
        String eventId = doc.getId();

        String title = doc.getString("title");
        String date = doc.getString("date");
        String imageUrl = doc.getString("imageUrl");
        String time = doc.getString("time");
        String location = doc.getString("location");

        holder.tvTitle.setText(title != null ? title : "Event Name");
        holder.tvDate.setText(date != null ? date : "Date TBA");

        if (holder.tvTime != null) {
            holder.tvTime.setText(time != null ? time : "Time TBA");
        }

        if (holder.tvLocation != null) {
            holder.tvLocation.setText(location != null ? location : "AGPIT Solapur");
        }

// 5. Load Image with Glide and fix the "Teal Square" issue
        if (holder.ivEventBanner != null) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // 🟢 REMOVE the XML tint so the actual photo colors show
                holder.ivEventBanner.setImageTintList(null);

                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_event_note) // Better placeholder
                        .centerCrop()
                        .into(holder.ivEventBanner);
            } else {
                // 🟡 RESTORE the tint if there is no image (shows the icon in Teal)
                holder.ivEventBanner.setImageResource(R.drawable.ic_event_note);
                holder.ivEventBanner.setImageTintList(android.content.res.ColorStateList.valueOf(
                        holder.itemView.getContext().getResources().getColor(R.color.teal_700)));
            }
        }

        // --- 1. Regular Click (Details) ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("eventId", eventId);
            v.getContext().startActivity(intent);
        });

        // --- 2. Long Click (Admin Delete) ---
        if (isAdmin) {
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteConfirmation(v, eventId, position);
                return true; // consumes the click
            });
        }

        // Animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(400).start();
    }

    private void showDeleteConfirmation(View v, String eventId, int position) {
        new MaterialAlertDialogBuilder(v.getContext())
                .setTitle("Delete Event")
                .setMessage("Remove this event permanently?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection("events")
                            .document(eventId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(v.getContext(), "Event Deleted", Toast.LENGTH_SHORT).show();

                                // 1. Remove from RecyclerView
                                if (position < events.size()) {
                                    // Get the image URL before removing the document
                                    String deletedImageUrl = events.get(position).getString("imageUrl");

                                    events.remove(position);
                                    notifyItemRemoved(position);

                                    // 2. Sync with the Activity's Slider (Optional but Recommended)
                                    if (v.getContext() instanceof EventsActivity) {
                                        EventsActivity activity = (EventsActivity) v.getContext();
                                        activity.removeEventFromSlider(deletedImageUrl);
                                    }
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
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
            tvTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            ivEventBanner = itemView.findViewById(R.id.ivEventBanner);
        }
    }
}