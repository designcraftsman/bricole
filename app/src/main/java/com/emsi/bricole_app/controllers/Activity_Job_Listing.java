package com.emsi.bricole_app.controllers;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emsi.bricole_app.R;

import java.util.ArrayList;
import java.util.List;

import com.emsi.bricole_app.models.Job;

public class Activity_Job_Listing extends Employee_Drawer {

    private RecyclerView jobRecyclerView;
    private Spinner citySpinner;
    private EditText searchInput;
    private Activity_JobAdapter adapter;
    private List<Job> jobList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_job_listing);

        // Initialize views
        jobRecyclerView = findViewById(R.id.jobRecyclerView);
        searchInput = findViewById(R.id.searchInput);

        // Set up RecyclerView
        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        jobList = new ArrayList<>();
        jobList.add(new Job("Berenice", "Électricien – Installation et Réparation", "Casablanca", "Il y a 1 jour", true));
        jobList.add(new Job("Ali", "Plombier – Réparation de fuites", "Rabat", "Il y a 3 jours", false));

        adapter = new Activity_JobAdapter(jobList);
        jobRecyclerView.setAdapter(adapter);
    }
}
