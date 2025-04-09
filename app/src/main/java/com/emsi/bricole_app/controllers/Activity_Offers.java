package com.emsi.bricole_app.controllers;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.emsi.bricole_app.R;

public class Activity_Offers extends Employer_Drawer {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method is defined in BaseDrawerActivity and inflates the DrawerLayout
        setupDrawer(R.layout.activity_offers);

        // Create adapter with custom layout for selected item display
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_options,       // defined in res/values/strings.xml
                R.layout.component_spinner        // your custom layout
        );
    }
}
