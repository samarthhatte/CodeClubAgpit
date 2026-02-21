package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.fragments.Community_Fragment;
import com.agpitcodeclub.codeclubagpit.ui.fragments.HomeFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePage extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private String currentRole = "member"; // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 1. Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 2. Navigation View & Header
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        ShapeableImageView navUserImage = headerView.findViewById(R.id.nav_user_image);

        // 3. Fetch User Data & Handle Role-Based Menu
        if (mAuth.getCurrentUser() != null) {
            String currentUid = mAuth.getCurrentUser().getUid();
            FirebaseFirestore.getInstance().collection("users").document(currentUid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            currentRole = doc.getString("role");
                            String name = doc.getString("name");
                            String profilePic = doc.getString("profilePic");

                            // Set Name
                            if (name != null) navUserName.setText("Welcome, " + name);

                            // Set Profile Pic
                            if (profilePic != null && !profilePic.isEmpty()) {
                                Glide.with(this).load(profilePic).placeholder(R.drawable.ic_user_placeholder).circleCrop().into(navUserImage);
                            }

                            // Update Menu Visibility Safely
                            Menu navMenu = navigationView.getMenu();
                            boolean isAdmin = "admin".equals(currentRole) || "super_admin".equals(currentRole);
                            boolean isSuperAdmin = "super_admin".equals(currentRole);

                            if (navMenu.findItem(R.id.nav_add_events) != null)
                                navMenu.findItem(R.id.nav_add_events).setVisible(isAdmin);

                            if (navMenu.findItem(R.id.nav_notifications) != null)
                                navMenu.findItem(R.id.nav_notifications).setVisible(isAdmin);

                            if (navMenu.findItem(R.id.nav_manage_admins) != null)
                                navMenu.findItem(R.id.nav_manage_admins).setVisible(isSuperAdmin);

                            if (navMenu.findItem(R.id.nav_add_images_to_gallery) != null)
                                navMenu.findItem(R.id.nav_add_images_to_gallery).setVisible(isAdmin);
                        }
                    })
                    .addOnFailureListener(e -> navUserName.setText("Welcome, Member"));
        }

        // 4. Navigation Item Selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_community) {
                replaceFragment(new Community_Fragment());
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_members) {
                // FIXED: Simplified logic for members activity
                Intent intent = new Intent(this, MembersActivity.class);
                intent.putExtra("role", currentRole); // Always pass the role
                startActivity(intent);
            } else if (id == R.id.nav_admin) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else if (id == R.id.nav_add_events) {
                startActivity(new Intent(this, AddEventActivity.class));
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, AdminPushActivity.class));
            } else if (id == R.id.nav_manage_admins) {
                startActivity(new Intent(this, ManageAdminsActivity.class));
            } else if (id == R.id.nav_gallery) {
                startActivity(new Intent(this, GalleryActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutUsActivity.class));
            } else if (id == R.id.nav_add_images_to_gallery) {
                startActivity(new Intent(this, AdminGallery.class));
            } else if (id == R.id.nav_events) {
                startActivity(new Intent(this, EventsActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 5. Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) replaceFragment(new HomeFragment());
            else if (item.getItemId() == R.id.nav_community) replaceFragment(new Community_Fragment());
            return true;
        });

        // Default Fragment
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        checkNotificationPermission();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}