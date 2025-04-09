package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.emsi.bricole_app.R;

public class Activity_Profile_Settings  extends BaseDrawerActivity{

    private ImageButton mBackBtn;

    private LinearLayout mQualificationsSection;

    private LinearLayout mPreferenceSection;

    private LinearLayout mAvailabilitySection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method is defined in BaseDrawerActivity and inflates the DrawerLayout
        setupDrawer(R.layout.activity_profile_settings);

        mQualificationsSection = findViewById(R.id.qualificationsSection);

        mPreferenceSection = findViewById(R.id.preferencesSection);

        mAvailabilitySection = findViewById(R.id.availabilitySection);

        mPreferenceSection.setOnClickListener(view->{
            Intent intent = new Intent(this, Activity_Profile_Preference.class);
            startActivity(intent);
        });

        mQualificationsSection.setOnClickListener(view->{
            Intent intent = new Intent(this, Activity_Profile_Qualifications.class);
            startActivity(intent);
        });

        mAvailabilitySection.setOnClickListener(view->{
            Intent intent = new Intent(this, Activity_Profile_Employee_Availability.class);
            startActivity(intent);
        });

        mBackBtn = findViewById(R.id.backButton);

        mBackBtn.setOnClickListener(view->{
            finish();
        });


    }
}
