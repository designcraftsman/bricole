package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.emsi.bricole_app.R;

public class Activity_Profile_Settings  extends BaseDrawerActivity{

    private ImageButton mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method is defined in BaseDrawerActivity and inflates the DrawerLayout
        setupDrawer(R.layout.activity_profile_settings);

        mBackBtn = findViewById(R.id.backButton);

        mBackBtn.setOnClickListener(view->{
            finish();
        });


    }
}
