package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.UserModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.TeamViewHolder> {

    private final List<UserModel> teamList;

    public TeamMemberAdapter(List<UserModel> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_member, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        UserModel user = teamList.get(position);

        holder.tvName.setText(user.getName());

        // Handling the Role Tag
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            holder.tvRoleTag.setText(user.getRole());
            holder.tvRoleTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvRoleTag.setText("Member");
        }

        // Loading the Image with Glide
        if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfilePic())
                    .placeholder(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(holder.imgMember);
        } else {
            holder.imgMember.setImageResource(R.drawable.ic_user_placeholder);
        }

        // Optional: Click to see details or chat
        holder.itemView.setOnClickListener(v -> {
            // Intent to ChatActivity or ProfileDetail
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMember;
        TextView tvName, tvRoleTag;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMember = itemView.findViewById(R.id.memberImage);
            tvName = itemView.findViewById(R.id.memberName);
            tvRoleTag = itemView.findViewById(R.id.memberRole);
        }
    }
}