package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.graphics.Color;

import com.agpitcodeclub.codeclubagpit.ui.activities.ChatActivity;
import com.agpitcodeclub.codeclubagpit.ui.activities.FullScreenImageActivity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.UserModel;
import com.agpitcodeclub.codeclubagpit.ui.activities.MembersActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private final List<UserModel> memberList;
    private final boolean isAdmin;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final boolean isSuperAdmin;
    private final String currentUid;



    public MemberAdapter(List<UserModel> memberList, boolean isAdmin,  boolean isSuperAdmin, String currentUid) {
        this.memberList = memberList;
        this.isAdmin = isAdmin;
        this.isSuperAdmin = isSuperAdmin;
        this.currentUid = currentUid;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        UserModel user = memberList.get(position);

        // 1️⃣ Basic info (everyone sees this)
        holder.nameText.setText(user.getName());

        String profileUrl = user.getProfilePic();
        // --- TAG LOGIC (Aesthetic & Editable) ---
        TextView txtRoleTag = holder.itemView.findViewById(R.id.txtRoleTag);
        String customTitle = user.getCustomTitle();
        String baseRole = user.getRole();

        // 1. Display Logic
        if (customTitle != null && !customTitle.isEmpty()) {
            txtRoleTag.setText(customTitle);
            txtRoleTag.setTextColor(Color.parseColor("#FFD700")); // Gold for special titles
        } else {
            txtRoleTag.setText(baseRole != null ? baseRole : "STUDENT");
            txtRoleTag.setTextColor(Color.BLACK); // Default black for standard roles
        }

        // 2. Click Logic for Admins
        txtRoleTag.setOnClickListener(v -> {
            if (isAdmin || isSuperAdmin) {
                showEditTagDialog(v, user);
            }
        });

// ✅ Load profile image
        if (profileUrl != null && !profileUrl.isEmpty()) {

            Glide.with(holder.itemView.getContext())
                    .load(profileUrl)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(holder.imgMember);

            // ✅ Open Full Screen on Click
            holder.imgMember.setOnClickListener(v -> {

                Intent intent = new Intent(
                        holder.itemView.getContext(),
                        FullScreenImageActivity.class
                );

                intent.putExtra("IMAGE_URL", profileUrl);

                holder.itemView.getContext().startActivity(intent);
            });

        } else {

            holder.imgMember.setImageResource(R.drawable.ic_user_placeholder);

            // ❌ No photo available
            holder.imgMember.setOnClickListener(v ->
                    Toast.makeText(holder.itemView.getContext(),
                            "Profile photo not available",
                            Toast.LENGTH_SHORT).show()
            );
        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    ChatActivity.class
            );

            intent.putExtra("uid", user.getId());   // Receiver UID
            intent.putExtra("name", user.getName()); // Receiver Name
            intent.putExtra("profilePic", user.getProfilePic());
            holder.itemView.getContext().startActivity(intent);
        });



        if (user.getSkills() != null) {
            holder.skillsText.setText(String.join(", ", user.getSkills()));
        } else {
            holder.skillsText.setText("");
        }

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

        // 2️⃣ Admin actions
        holder.btnAdminAction.setVisibility(View.GONE);

        if (!isAdmin) return;

        // 🚫 Prevent self role change
        if (user.getId().equals(currentUid)) return;

        switch (user.getRole()) {
            case "student":
                holder.btnAdminAction.setText("Make Member");
                holder.btnAdminAction.setVisibility(View.VISIBLE);
                holder.btnAdminAction.setOnClickListener(v ->
                        confirmRoleChange(v, user.getId(), "member"));
                break;

            case "member":
                if (isSuperAdmin) {
                    holder.btnAdminAction.setText("Make Admin");
                    holder.btnAdminAction.setVisibility(View.VISIBLE);
                    holder.btnAdminAction.setOnClickListener(v ->
                            confirmRoleChange(v, user.getId(), "admin"));
                } else {
                    holder.btnAdminAction.setText("Make Alumni");
                    holder.btnAdminAction.setVisibility(View.VISIBLE);
                    holder.btnAdminAction.setOnClickListener(v ->
                            confirmRoleChange(v, user.getId(), "alumni"));
                }
                break;

            case "admin":
                if (isSuperAdmin) {
                    holder.btnAdminAction.setText("Remove Admin");
                    holder.btnAdminAction.setVisibility(View.VISIBLE);
                    holder.btnAdminAction.setOnClickListener(v ->
                            confirmRoleChange(v, user.getId(), "member"));
                }
                break;
        }
    }



    private void confirmRoleChange(View v, String userId, String newRole) {
        new AlertDialog.Builder(v.getContext())
                .setTitle("Confirm Action")
                .setMessage("Change role to " + newRole.toUpperCase() + "?")
                .setPositiveButton("Yes", (d, w) ->
                        db.collection("users")
                                .document(userId)
                                .update("role", newRole)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(
                                            v.getContext(),
                                            "Role updated",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // 🔁 Switch tab based on new role
                                    if (v.getContext() instanceof MembersActivity) {
                                        MembersActivity activity =
                                                (MembersActivity) v.getContext();
                                        activity.switchToRoleTab(newRole);
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                v.getContext(),
                                                e.getMessage(),
                                                Toast.LENGTH_SHORT
                                        ).show()
                                )
                )
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void openLink(View v, String url, String label) {
        if (url != null && !url.isEmpty()) {
            v.getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            Toast.makeText(v.getContext(),
                    "User has not set " + label,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgMember;
        TextView nameText, skillsText, txtRoleTag;
        ImageButton btnGithub, btnLinkedIn, btnEmail, btnPortfolio;
        Button btnAdminAction;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMember = itemView.findViewById(R.id.imgMember);
            nameText = itemView.findViewById(R.id.txtName);
            txtRoleTag = itemView.findViewById(R.id.txtRoleTag); // Initialize here
            skillsText = itemView.findViewById(R.id.txtSkills);
            btnGithub = itemView.findViewById(R.id.btnGithub);
            btnLinkedIn = itemView.findViewById(R.id.btnLinkedIn);
            btnEmail = itemView.findViewById(R.id.btnEmail);
            btnPortfolio = itemView.findViewById(R.id.btnPortfolio);
            btnAdminAction = itemView.findViewById(R.id.btnAdminAction);
        }
    }
    private void showEditTagDialog(View v, UserModel user) {
        String[] suggestions = {
                "President", "Vice President", "Secretary", "Treasurer",
                "Android Lead", "Web Lead", "AI/ML Lead", "Design Lead", "Code Club Lead",
                "Technical Head", "Event Coordinator"
        };
        // Create the input view with Prachi's aesthetic in mind
        AutoCompleteTextView input = new AutoCompleteTextView(v.getContext());
        input.setText(user.getCustomTitle());
        input.setHint("Enter position...");

        ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<>(v.getContext(),
                android.R.layout.simple_dropdown_item_1line, suggestions);
        input.setAdapter(suggestionAdapter);
        input.setThreshold(1); // Suggest after 1 letter

        // Layout container for padding in dialog
        FrameLayout container = new FrameLayout(v.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 60; params.rightMargin = 60; params.topMargin = 30;
        input.setLayoutParams(params);
        container.addView(input);

        new MaterialAlertDialogBuilder(v.getContext())
                .setTitle("Assign Custom Title")
                .setMessage("Define " + user.getName() + "'s specific role in the club.")
                .setView(container)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newTitle = input.getText().toString().trim();
                    db.collection("users").document(user.getId())
                            .update("customTitle", newTitle)
                            .addOnSuccessListener(aVoid -> Toast.makeText(v.getContext(), "Role Assigned!", Toast.LENGTH_SHORT).show());
                })
                .setNeutralButton("Clear", (dialog, which) -> {
                    db.collection("users").document(user.getId())
                            .update("customTitle", "")
                            .addOnSuccessListener(aVoid -> Toast.makeText(v.getContext(), "Custom role removed", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

