package com.emsi.bricole_app.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.emsi.bricole_app.R;
import com.google.android.material.navigation.NavigationView;

public class Drawer extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Don't call setContentView here directly
    }

    protected void setupDrawer(int layoutResID) {
        // Get user role from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        userRole = prefs.getString("role",null); // default to Employee if missing
        // Choose layout based on role

        System.out.println("User role is :" + userRole);

        boolean isEmployer = userRole != null && userRole.equalsIgnoreCase("employer");

        int drawerLayoutRes = isEmployer ?
                R.layout.employer_drawer_layout : R.layout.employee_drawer_layout;


        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(drawerLayoutRes, null);
        FrameLayout container = fullLayout.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, container, true);
        super.setContentView(fullLayout);

        ImageView profileImage = findViewById(R.id.profile_image);
        Component_Nav.loadUserProfileImage(this, profileImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView notificationsBtn = findViewById(R.id.notifications);
        notificationsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Activity_Notifications.class);
            startActivity(intent);
        });

        drawerLayout = fullLayout.findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupNavigationMenu();
    }

    private void setupNavigationMenu() {
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (userRole != null && userRole.equalsIgnoreCase("employer"))
            {
                handleEmployerNavigation(id);
            } else {
                handleEmployeeNavigation(id);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void handleEmployerNavigation(int id) {
        if (id == R.id.nav_candidates) {
            startActivity(new Intent(this, Activity_Candidates.class));
        } else if (id == R.id.nav_offers) {
            startActivity(new Intent(this, Activity_Offers.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, Activity_Profile_Settings.class));
        } else if (id == R.id.nav_messages) {
            startActivity(new Intent(this, Activity_Messages_Listing.class));
        }else if (id == R.id.nav_signout) {
            // Clear SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Redirect to SignIn Activity
            Intent intent = new Intent(this, Activity_Signin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear backstack
            startActivity(intent);
            finish(); // Finish current activity
        }

    }

    private void handleEmployeeNavigation(int id) {
       if (id == R.id.nav_applications) {
            startActivity(new Intent(this, Activity_Applications.class));
        } else if (id == R.id.nav_offers) {
            startActivity(new Intent(this, Activity_Job_Listing.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, Activity_Profile_Settings.class));
        } else if (id == R.id.nav_messages) {
            startActivity(new Intent(this, Activity_Messages_Listing.class));
        }else if (id == R.id.nav_signout) {
            // Clear SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Redirect to SignIn Activity
            Intent intent = new Intent(this, Activity_Signin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear backstack
            startActivity(intent);
            finish(); // Finish current activity
        }

    }
}
