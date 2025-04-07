package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.emsi.bricole_app.R;
import com.google.android.material.navigation.NavigationView;

public class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Don't call setContentView here directly
    }

    protected void setupDrawer(int layoutResID) {
        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.base_drawer_layout, null);
        FrameLayout container = fullLayout.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, container, true);
        super.setContentView(fullLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = fullLayout.findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(item -> {
            // handle menu item clicks here
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, Activity_Employer_Dashboard.class));
            }else if(id == R.id.nav_candidates){
                startActivity(new Intent(this, Activity_Candidates.class));
            }else if(id == R.id.nav_offers){
                startActivity(new Intent(this, Activity_Offers.class));
            }else if(id == R.id.nav_settings){
                startActivity(new Intent(this, Activity_Profile_Settings.class));
            }else if(id == R.id.nav_messages){
                startActivity(new Intent(this, Activity_Messages_Listing.class));
            }
            // Add more menu actions here
            drawerLayout.closeDrawers();
            return true;
        });
    }
}

