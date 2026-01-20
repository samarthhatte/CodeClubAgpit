package com.agpitcodeclub.codeclubagpit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Map<String, Object>> events;

    public EventAdapter(List<Map<String, Object>> events) {
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
        Map<String, Object> event = events.get(position);

        holder.tvTitle.setText((String) event.get("title"));
        holder.tvDate.setText((String) event.get("date"));

        // Handling the click on the Card
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), EventsActivity.class);
            // Passing the data to the next screen
            intent.putExtra("title", (String) event.get("title"));
            intent.putExtra("date", (String) event.get("date"));
            intent.putExtra("description", (String) event.get("description"));
            v.getContext().startActivity(intent);
        });

        // Your existing animation
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