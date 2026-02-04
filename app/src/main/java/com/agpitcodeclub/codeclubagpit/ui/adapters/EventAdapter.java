package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventDetailActivity;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventsActivity;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.List;
import java.util.Map;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<DocumentSnapshot> events;

    public EventAdapter(List<DocumentSnapshot> events) {
        this.events = events;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Now using your custom attractive card layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DocumentSnapshot doc = events.get(position);

        holder.tvTitle.setText(doc.getString("title"));
        holder.tvDate.setText(doc.getString("date"));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("eventId", doc.getId()); // ⭐ KEY CHANGE
            v.getContext().startActivity(intent);
        });

        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(400).start();
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);

            // Lead Strategy: Set generic listener here,
            // then use getAdapterPosition() to get the data.
        }
    }


}