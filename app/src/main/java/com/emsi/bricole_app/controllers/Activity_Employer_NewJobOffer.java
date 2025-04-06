package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.emsi.bricole_app.R;

public class Activity_Employer_NewJobOffer extends BaseDrawerActivity {

    private  TextView mBtnLoginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_employer_new_job_offer); // Set the correct layout
    }
}
