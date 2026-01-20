package com.agpitcodeclub.codeclubagpit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private final List<UserModel> memberList;

    public MemberAdapter(List<UserModel> memberList, boolean isAdmin) {
        this.memberList = memberList;
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
        // Set up button click listeners
        holder.btnGithub.setOnClickListener(v ->
                openLink(v, user.getGithub(), "GitHub profile"));

        holder.btnLinkedIn.setOnClickListener(v ->
                openLink(v, user.getLinkedin(), "LinkedIn profile"));

        holder.btnPortfolio.setOnClickListener(v ->
                openLink(v, user.getPortfolio(), "Portfolio website"));

        holder.btnEmail.setOnClickListener(v -> {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + user.getEmail()));
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(),
                        "User has not set email yet",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgMember;   // ✅ ADD THIS
        TextView nameText, skillsText;
        ImageButton btnGithub, btnLinkedIn, btnEmail, btnPortfolio;


        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMember = itemView.findViewById(R.id.imgMember); // ✅ BIND IT
            nameText = itemView.findViewById(R.id.txtName);
            skillsText = itemView.findViewById(R.id.txtSkills);
            btnGithub = itemView.findViewById(R.id.btnGithub);
            btnLinkedIn = itemView.findViewById(R.id.btnLinkedIn);
            btnEmail = itemView.findViewById(R.id.btnEmail);
            btnPortfolio = itemView.findViewById(R.id.btnPortfolio);

        }
    }

    private void openLink(View v, String url, String label) {
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            v.getContext().startActivity(intent);
        } else {
            Toast.makeText(v.getContext(),
                    "User has not set " + label,
                    Toast.LENGTH_SHORT).show();
        }
    }


}