package com.emsi.bricole_app.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.emsi.bricole_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_Job_Details extends Employer_Drawer {

    private final String TAG = "Activity_Job_Details";
    private final String API_URL_BASE = "http://10.0.2.2:8080/api/main/jobs/search/";

    private TextView txtPostedBy, txtTitle, txtLocation, txtMission1, txtMission2, txtMission3, txtDescription;

    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_job_details);

        // Get views
        txtPostedBy = findViewById(R.id.txtPostedBy);
        txtTitle = findViewById(R.id.txtTitle);
        txtLocation = findViewById(R.id.txtLocation);
        txtMission1 = findViewById(R.id.txtMission1);
        txtMission2 = findViewById(R.id.txtMission2);
        txtMission3 = findViewById(R.id.txtMission3);
        txtDescription = findViewById(R.id.txtDescription);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        int offerId = getIntent().getIntExtra("offer_id", -1);
        if (offerId != -1) {
            fetchJobDetails(offerId);
        }
    }

    private void fetchJobDetails(int offerId) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL_BASE + offerId)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
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

                    runOnUiThread(() -> {
                        try {
                            JSONObject job = new JSONObject(jobJson);

                            String postedBy = job.getJSONObject("employer").getString("name");
                            String title = job.getString("title");
                            String location = job.getString("location");
                            String description = job.getString("description");

                            JSONArray missions = job.getJSONArray("missions");

                            txtPostedBy.setText("Par " + postedBy);
                            txtTitle.setText(title);
                            txtLocation.setText(location);
                            txtDescription.setText(description);

                            // Optional: display up to 3 missions
                            if (missions.length() > 0) txtMission1.setText("✔️ " + missions.getString(0));
                            if (missions.length() > 1) txtMission2.setText("✔️ " + missions.getString(1));
                            if (missions.length() > 2) txtMission3.setText("✔️ " + missions.getString(2));

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON error: " + e.getMessage());
                        }
                    });

                } else {
                    Log.e(TAG, "Job details API response not successful: Code " + response.code());
                }
            }
        });
    }
}
