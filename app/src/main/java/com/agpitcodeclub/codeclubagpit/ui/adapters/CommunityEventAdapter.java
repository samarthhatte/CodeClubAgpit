package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CommunityEventAdapter extends RecyclerView.Adapter<CommunityEventAdapter.ViewHolder> {

    private List<DocumentSnapshot> eventList;

    public CommunityEventAdapter(List<DocumentSnapshot> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = eventList.get(position);

        // Matching your DB keys
        String title = doc.getString("title");
        String date = doc.getString("date");
        String imageUrl = doc.getString("imageUrl");

        // Optional fields - providing defaults if missing in DB
        String time = doc.getString("time") != null ? doc.getString("time") : "TBA";
        String location = doc.getString("location") != null ? doc.getString("location") : "AGPIT Campus";

        holder.tvTitle.setText(title);
        holder.tvDate.setText(date);
        holder.tvTime.setText(time);
        holder.tvLocation.setText(location);

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.img) // Your default placeholder
                .into(holder.imgEvent);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEvent;
        TextView tvTitle, tvDate, tvTime, tvLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEvent = itemView.findViewById(R.id.eventImage);
            tvTitle = itemView.findViewById(R.id.eventTitle);
            tvDate = itemView.findViewById(R.id.eventDate);
            tvTime = itemView.findViewById(R.id.eventTime);
            tvLocation = itemView.findViewById(R.id.eventLocation);
        }
    }
}