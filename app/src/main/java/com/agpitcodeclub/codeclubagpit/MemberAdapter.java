package com.agpitcodeclub.codeclubagpit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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
        TextView nameText, skillsText;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.txtName);
            skillsText = itemView.findViewById(R.id.txtSkills);
        }
    }
}