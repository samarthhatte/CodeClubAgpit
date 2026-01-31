package com.agpitcodeclub.codeclubagpit.ui.adapters;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ManageAdminAdapter
        extends RecyclerView.Adapter<ManageAdminAdapter.AdminViewHolder> {

    private final List<UserModel> adminList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ManageAdminAdapter(List<UserModel> adminList) {
        this.adminList = adminList;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_admin, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        UserModel user = adminList.get(position);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());

        holder.btnRemoveAdmin.setOnClickListener(v -> {

            if ("super_admin".equals(user.getRole())) {
                Toast.makeText(v.getContext(),
                        "Super admin cannot be removed",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Remove Admin")
                    .setMessage("Revoke admin access from this user?")
                    .setPositiveButton("Yes", (d, w) ->
                            db.collection("users")
                                    .document(user.getId())
                                    .update("role", "member")
                                    .addOnSuccessListener(a ->
                                            Snackbar.make(v, "Admin removed", LENGTH_LONG).show()

                                    )
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
        });

    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        Button btnRemoveAdmin;

        AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            email = itemView.findViewById(R.id.txtEmail);
            btnRemoveAdmin = itemView.findViewById(R.id.btnRemoveAdmin);
        }
    }
}