package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.emsi.bricole_app.R;

public class Activity_Offers  extends BaseDrawerActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method is defined in BaseDrawerActivity and inflates the DrawerLayout
        setupDrawer(R.layout.activity_offers);
        Spinner spinner = findViewById(R.id.spinnerStatus);

        // Create adapter with custom layout for selected item display
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_options,       // defined in res/values/strings.xml
                R.layout.component_spinner        // your custom layout
        );

        // Optional: customize dropdown layout as well (use the same or another layout)
        adapter.setDropDownViewResource(R.layout.component_spinner);

        spinner.setAdapter(adapter);




    }
}
