package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {

    private List<DocumentSnapshot> teamList;

    public TeamAdapter(List<DocumentSnapshot> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = teamList.get(position);

        // Get data from Firestore
        String name = doc.getString("name");
        String role = doc.getString("role");
        String imageUrl = doc.getString("profileImageUrl"); // Ensure this matches your DB key

        holder.tvName.setText(name != null ? name : "Member");
        holder.tvRole.setText(role != null ? role : "Code Club Member");

        // Set the Golden Stroke color for the image
        holder.imgMember.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFD700")));

        // Load profile image
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_people) // Use your existing people icon
                .circleCrop()
                .into(holder.imgMember);
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgMember;
        TextView tvName, tvRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMember = itemView.findViewById(R.id.memberImage);
            tvName = itemView.findViewById(R.id.memberName);
            tvRole = itemView.findViewById(R.id.memberRole);
        }
    }
}