package com.emsi.bricole_app.controllers;

import android.os.Bundle;

import com.emsi.bricole_app.R;

public class Activity_Profile_Preference extends Employer_Drawer {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This method is defined in BaseDrawerActivity and inflates the DrawerLayout
        setupDrawer(R.layout.activity_profile_preference);
    }
}
