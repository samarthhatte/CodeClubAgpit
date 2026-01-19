package com.agpitcodeclub.codeclubagpit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<UserModel> memberList;
    private boolean isAdmin;

    public MemberAdapter(List<UserModel> memberList, boolean isAdmin) {
        this.memberList = memberList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        UserModel user = memberList.get(position);
        holder.nameText.setText(user.getName());

        String imageUrl = user.getProfilePic();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(holder.imgMember);
        } else {
            holder.imgMember.setImageResource(R.drawable.ic_user_placeholder);
        }

        // Convert skills list to a single string
        if (user.getSkills() != null) {
            holder.skillsText.setText(String.join(", ", user.getSkills()));
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgMember;   // ✅ ADD THIS
        TextView nameText, skillsText;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMember = itemView.findViewById(R.id.imgMember); // ✅ BIND IT
            nameText = itemView.findViewById(R.id.txtName);
            skillsText = itemView.findViewById(R.id.txtSkills);
        }
    }

}