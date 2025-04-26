package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.os.Bundle;

import com.emsi.bricole_app.R;
import android.widget.Button;

public class Activity_Employer_Dashboard extends Drawer {
    private Button mBtnNewJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method is defined in BaseDrawerActivity and inflates the DrawerLayout
        setupDrawer(R.layout.activity_employer_dashboard);

        mBtnNewJob = findViewById(R.id.btn_post_job);
        mBtnNewJob.setOnClickListener(view -> {
            Intent intent = new Intent(this, Activity_Employer_NewJobOffer.class);
            startActivity(intent);
        });
    }
}

