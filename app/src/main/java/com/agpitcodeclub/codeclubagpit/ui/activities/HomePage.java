package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.fragments.HomeFragment;
import com.agpitcodeclub.codeclubagpit.ui.fragments.Community_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 1. Setup Three-Line Slider (Drawer)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 2. Drawer Item Selection (Doc Scanner, etc.)
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()).commit();
             } else if (id == R.id.nav_community) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Community_Fragment()).commit();
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
                else if (id == R.id.nav_settings) {
                //    startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_about) {
                    startActivity(new Intent(this, AboutUsActivity.class));
            } else if (id == R.id.nav_gallery) {
                startActivity(new Intent(this, GalleryActivity.class));
            } else if (id == R.id.nav_events) {
                startActivity(new Intent(this, EventsActivity.class));
            } else if (id == R.id.nav_members) {
                startActivity(new Intent(this, MembersActivity.class));
            } else if (id == R.id.nav_admin) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 1. Check for Notification Permissions (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }


        // 3. Bottom Navigation (Her Home/Community designs)
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            if (item.getItemId() == R.id.nav_home) selected = new HomeFragment();
            else if (item.getItemId() == R.id.nav_community) selected = new Community_Fragment();

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected).commit();
            }
            return true;
        });

        // Default: Load Home on launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        // Note: Move your FCM subscription and loadUserName logic from MainActivity to here
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