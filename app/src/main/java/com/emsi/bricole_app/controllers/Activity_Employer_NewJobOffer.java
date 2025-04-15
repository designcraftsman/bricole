package com.emsi.bricole_app.controllers;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.toolbox.JsonObjectRequest;
import com.emsi.bricole_app.R;
import com.google.android.material.textfield.TextInputLayout;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Employer_NewJobOffer extends Employer_Drawer {

    private TextView mBtnLoginRedirect;

    private EditText mJobTitle;
    private EditText mJobDescription;
    private EditText mJobSalary;
    private EditText mJobLocation;
    private AutoCompleteTextView mJobCategoryOptions;
    private LinearLayout missionsContainer;
    private LinearLayout mediaContainer;
    private SharedPreferences prefs;

    private Button addMission;
    private Button addMedia;
    final String API_URL = "http://10.0.2.2:8080/api/employer/job/create";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_employer_new_job_offer);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        final String USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        // Initialize views
        mJobTitle = findViewById(R.id.job_offer_title);
        mJobSalary = findViewById(R.id.job_offer_salary);
        mJobDescription = findViewById(R.id.job_offer_description);
        mJobCategoryOptions = findViewById(R.id.job_offer_category_options);
        mJobLocation = findViewById(R.id.job_offer_location);
        missionsContainer = findViewById(R.id.missions_container);
        mediaContainer = findViewById(R.id.media_container);

        addMission = findViewById(R.id.add_mission_button);
        addMedia = findViewById(R.id.add_media_button);
        addMission.setOnClickListener(v -> addMissionField());
        addMedia.setOnClickListener(v -> addMediaField());

        Button publishBtn = findViewById(R.id.btn_new_job);
        publishBtn.setOnClickListener(view -> submitJobOffer(USER_ACCESS_TOKEN));
        // Category dropdown setup
        String[] category_options = {"PLUMBING", "ELECTRICAL", "CARPENTRY", "PAINTING", "GARDENING", "CLEANING", "MOVING", "OTHER"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, category_options);
        mJobCategoryOptions.setAdapter(adapter);
        mJobCategoryOptions.setOnClickListener(v -> mJobCategoryOptions.showDropDown());
        mJobCategoryOptions.setFocusable(false);
        mJobCategoryOptions.setInputType(0);
    }

    private void addMissionField() {
        EditText missionInput = new EditText(this);
        missionInput.setHint("Mission");
        missionInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        missionInput.setBackgroundResource(R.drawable.input_field_background); // Optional: style it
        missionsContainer.addView(missionInput);
    }

    private void addMediaField() {
        EditText mediaInput = new EditText(this);
        mediaInput.setHint("Media URL");
        mediaInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        mediaInput.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        mediaInput.setBackgroundResource(R.drawable.input_field_background); // Optional
        mediaContainer.addView(mediaInput);
    }

    private void submitJobOffer(String token) {
        try {
            JSONObject body = new JSONObject();
            body.put("title", mJobTitle.getText().toString().trim());
            body.put("description", mJobDescription.getText().toString().trim());
            body.put("location", mJobLocation.getText().toString().trim());
            body.put("salary", Double.parseDouble(mJobSalary.getText().toString().trim()));
            body.put("category", mJobCategoryOptions.getText().toString().trim());

            // Missions array
            JSONArray missions = new JSONArray();
            for (int i = 0; i < missionsContainer.getChildCount(); i++) {
                EditText missionInput = (EditText) missionsContainer.getChildAt(i);
                String missionText = missionInput.getText().toString().trim();
                if (!missionText.isEmpty()) missions.put(missionText);
            }
            body.put("missions", missions);

            // Media object
            JSONObject media = new JSONObject();
            for (int i = 0; i < mediaContainer.getChildCount(); i++) {
                EditText mediaInput = (EditText) mediaContainer.getChildAt(i);
                String mediaUrl = mediaInput.getText().toString().trim();
                if (!mediaUrl.isEmpty()) media.put(String.valueOf(i), mediaUrl);
            }
            body.put("media", media);

            sendRequest(token, body);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest(String token, JSONObject body) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL, body,
                response -> Toast.makeText(this, "Offre publiée avec succès!", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(this, "Erreur de publication", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
