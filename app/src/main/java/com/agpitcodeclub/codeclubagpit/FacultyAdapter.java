package com.agpitcodeclub.codeclubagpit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {

    private final List<FacultyModel> facultyList;

    public FacultyAdapter(List<FacultyModel> facultyList) {
        this.facultyList = facultyList;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faculty, parent, false);
        return new FacultyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        FacultyModel model = facultyList.get(position);

        holder.tvName.setText(model.getName());
        holder.tvRole.setText(model.getRole());

        Glide.with(holder.itemView.getContext())
                .load(model.getImageUrl())
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .into(holder.imgFaculty);
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    static class FacultyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFaculty;
        TextView tvName, tvRole;

        FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFaculty = itemView.findViewById(R.id.imgFaculty);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
        }
    }
}
