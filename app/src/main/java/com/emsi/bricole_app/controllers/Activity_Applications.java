package com.emsi.bricole_app.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Application;
import com.emsi.bricole_app.models.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Applications extends Drawer {

    private RecyclerView recyclerView;
    private com.emsi.bricole_app.controllers.Activity_ApplicationAdapter adapter;
    private ArrayList<Application> applicationList;
    private static final String API_URL = "http://10.0.2.2:8080/api/employee/applications"; // use 10.0.2.2 for emulator
    private SharedPreferences prefs;
    private EditText searchInput;
    private String USER_ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_applications); // your XML layout file

        recyclerView = findViewById(R.id.jobRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        searchInput = findViewById(R.id.searchInput);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApplicationsByTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        applicationList = new ArrayList<>();
        adapter = new com.emsi.bricole_app.controllers.Activity_ApplicationAdapter(applicationList, (applicationId, position) -> {
            cancelApplication(applicationId, position);
        });
        recyclerView.setAdapter(adapter);


        fetchApplications();
    }

    private void filterApplicationsByTitle(String query) {
        List<Application> filteredList = new ArrayList<>();
        for (Application application : applicationList) {
            if (application.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(application);
            }
        }

        adapter.updateList(filteredList);
    }
    private void fetchApplications() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                response -> {
                    parseApplications(response);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to fetch applications", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + USER_ACCESS_TOKEN);
                return headers;
            }
        };

        queue.add(request);
    }

    private void parseApplications(JSONArray response) {
        try {
            applicationList.clear(); // Clear previous data
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                JSONObject jobObj = obj.getJSONObject("job");

                String state = obj.getString("state");
                if ("CANCELED".equalsIgnoreCase(state)) {
                    continue; // Skip canceled applications
                }

                int id = jobObj.getInt("id");
                String title = jobObj.getString("title");
                String location = jobObj.getString("location");
                String createdAt = jobObj.getString("createdAt");

                applicationList.add(new Application(id, title, location, createdAt, state));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing applications", Toast.LENGTH_SHORT).show();
        }
    }


    private void cancelApplication(int applicationId, int position) {
        String cancelUrl = "http://10.0.2.2:8080/api/employee/application/" + applicationId + "/cancel";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, cancelUrl, null,
                response -> {
                    Toast.makeText(this, "Candidature annulée avec succès", Toast.LENGTH_SHORT).show();
                    adapter.removeItem(position);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erreur lors de l'annulation", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + USER_ACCESS_TOKEN);
                return headers;
            }
        };

        queue.add(request);
    }

}
