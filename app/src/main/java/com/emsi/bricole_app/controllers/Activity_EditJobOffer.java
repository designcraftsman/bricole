package com.emsi.bricole_app.controllers;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Response;




public class Activity_EditJobOffer extends Drawer {

    private TextView mBtnLoginRedirect;
    private static final Logger logger = LoggerFactory.getLogger(Activity_EditJobOffer.class);
    private EditText mJobTitle;
    private EditText mJobDescription;
    private EditText mJobSalary;
    private EditText mJobLocation;
    private AutoCompleteTextView mJobCategoryOptions;
    private LinearLayout missionsContainer;
    private SharedPreferences prefs;
    private Button addMission;
    final String API_URL = "http://10.0.2.2:8080/api/employer/job";
    private final String TAG = "Activity_EditJobOffer";
    private final String API_URL_BASE = "http://10.0.2.2:8080/api/main/jobs/search/";
    private int jobId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_edit_job_offer);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        final String USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        jobId = getIntent().getIntExtra("job_id", -1);

        System.out.println("job id is :"+jobId);
        if (jobId != -1) {
            System.out.println("the data is gonna get fetched");
            fetchJobDetails(jobId); // 👈 Load previous data into the form
        }

        // Initialize views
        mJobTitle = findViewById(R.id.job_offer_title);
        mJobSalary = findViewById(R.id.job_offer_salary);
        mJobDescription = findViewById(R.id.job_offer_description);
        mJobCategoryOptions = findViewById(R.id.job_offer_category_options);
        mJobLocation = findViewById(R.id.job_offer_location);
        missionsContainer = findViewById(R.id.missions_container);

        addMission = findViewById(R.id.add_mission_button);
        addMission.setOnClickListener(v -> addMissionField());

        Button editBtn = findViewById(R.id.btn_update_job);

        editBtn.setOnClickListener(view -> {
            if (jobId != -1) {
                editJobOffer(USER_ACCESS_TOKEN, jobId);
            } else {
                Toast.makeText(this, "Job ID not provided!", Toast.LENGTH_SHORT).show();
            }
        });

        // Category dropdown setup
        String[] category_options = {"PLUMBING", "ELECTRICAL", "CARPENTRY", "PAINTING", "GARDENING", "CLEANING", "MOVING", "OTHER"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, category_options);
        mJobCategoryOptions.setAdapter(adapter);
        mJobCategoryOptions.setOnClickListener(v -> mJobCategoryOptions.showDropDown());
        mJobCategoryOptions.setFocusable(false);
        mJobCategoryOptions.setInputType(0);
    }


    private void fetchJobDetails(int offerId) {
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(API_URL_BASE + offerId)
                .addHeader("Authorization", "Bearer " + prefs.getString("access_token", null))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Job details API call failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jobJson = response.body().string();

                    // Log the raw JSON response to check
                    Log.d(TAG, "Job details response: " + jobJson);
                    System.out.println("Edit Job Data:" + jobJson);
                    runOnUiThread(() -> {
                        try {
                            JSONObject job = new JSONObject(jobJson);

                            // Populate fields
                            mJobTitle.setText(job.getString("title"));
                            mJobLocation.setText(job.getString("location"));
                            mJobDescription.setText(job.getString("description"));
                            mJobSalary.setText(String.valueOf(job.getDouble("salary")));
                            mJobCategoryOptions.setText(job.getString("category"), false);

                            // Missions array (ensure we don't attempt to add a mission field if empty)
                            JSONArray missions = job.getJSONArray("missions");
                            if (missions.length() > 0) {
                                for (int i = 0; i < missions.length(); i++) {
                                    addMissionField();
                                    EditText missionInput = (EditText) missionsContainer.getChildAt(i);
                                    missionInput.setText(missions.getString(i));
                                }
                            }


                        } catch (Exception e) {
                            Log.e(TAG, "Error processing JSON: " + e.getMessage());
                        }
                    });

                } else {
                    Log.e(TAG, "API call failed with response code: " + response.code());
                }
            }

        });
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


    private void editJobOffer(String token, int jobId) {
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


            sendUpdateRequest(token, body, jobId);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void sendUpdateRequest(String token, JSONObject body, int jobId) {
        String url = "http://10.0.2.2:8080/api/employer/job/" + jobId + "/update";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> Toast.makeText(this, "Offre modifiée avec succès!", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show()
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
