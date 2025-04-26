package com.emsi.bricole_app.controllers;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Job_Listing extends Drawer {

    private RecyclerView jobRecyclerView;
    private Spinner citySpinner;
    private EditText searchInput;
    private Activity_JobAdapter adapter;
    private List<Job> jobList;
    private RequestQueue requestQueue;

    private static final String JOBS_API_URL = "http://10.0.2.2:8080/api/main/jobs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_job_listing);

        jobRecyclerView = findViewById(R.id.jobRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        adapter = new Activity_JobAdapter(jobList);
        jobRecyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchJobsFromApi();
    }

    private void fetchJobsFromApi() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                JOBS_API_URL,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jobObject = response.getJSONObject(i);

                            String status = jobObject.getString("status");
                            if (!status.equals("OPEN")) continue;

                            int id = jobObject.getInt("id");
                            String title = jobObject.getString("title");
                            String description = jobObject.getString("description");
                            String location = jobObject.getString("location");
                            String createdAt = jobObject.getString("createdAt");

                            // Create Job object (update your Job model to match)
                            Job job = new Job(
                                    id,
                                    title,
                                    description,
                                    location,
                                    formatDate(createdAt),
                                    true // You can set this based on your logic
                            );

                            jobList.add(job);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur de parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erreur de connexion", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    // Optional: Format date string from API
    private String formatDate(String isoDate) {
        return "Publié: " + isoDate.substring(0, 10); // Just take yyyy-MM-dd
    }
}
