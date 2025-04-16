package com.emsi.bricole_app.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Activity_Single_Job_Offer extends Employee_Drawer {
    private ImageView backButton;
    private TextView txtRetour, txtPostedBy, txtTitle, txtLocation;
    private TextView txtMissionLabel, txtMission1, txtMission2, txtMission3;
    private TextView txtDescriptionLabel, txtDescription;
    private TextView txtSkillsLabel, txtSkill1, txtSkill2, txtSkill3;
    private TextView txtContact;
    private Button btnApply;

    private ScrollView scrollView;
    private LinearLayout buttonContainer;

    final private String API_URL = "http://10.0.2.2:8080/api/main/jobs/search/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_single_job_offer);

        int jobId = getIntent().getIntExtra("job_id", -1); // default -1 if not found
        if (jobId != -1) {
            fetchJobDetails(jobId);
        } else {
            Toast.makeText(this, "Job ID not found!", Toast.LENGTH_SHORT).show();
        }


        // Top bar
        backButton = findViewById(R.id.backButton);
        txtRetour = findViewById(R.id.txtRetour);

        // Job info
        txtPostedBy = findViewById(R.id.txtPostedBy); // Add android:id="@+id/txtPostedBy" if needed
        txtTitle = findViewById(R.id.txtTitle);       // Add android:id="@+id/txtTitle"
        txtLocation = findViewById(R.id.txtLocation); // Add android:id="@+id/txtLocation"

        // Mission section
        txtMissionLabel = findViewById(R.id.txtMissionLabel);
        txtMission1 = findViewById(R.id.txtMission1);
        txtMission2 = findViewById(R.id.txtMission2);
        txtMission3 = findViewById(R.id.txtMission3);

        // Description section
        txtDescriptionLabel = findViewById(R.id.txtDescriptionLabel);
        txtDescription = findViewById(R.id.txtDescription);

        // Skills section
        txtSkillsLabel = findViewById(R.id.txtSkillsLabel);
        txtSkill1 = findViewById(R.id.txtSkill1);
        txtSkill2 = findViewById(R.id.txtSkill2);
        txtSkill3 = findViewById(R.id.txtSkill3);

        // Contact
        txtContact = findViewById(R.id.txtContact);

        // ScrollView & button container
        scrollView = findViewById(R.id.scrollView);
        buttonContainer = findViewById(R.id.buttonContainer);

        // Apply button
        btnApply = findViewById(R.id.btnApply);

        // Example functionality
        btnApply.setOnClickListener(v ->
                Toast.makeText(this, "Vous avez postulé avec succès !", Toast.LENGTH_SHORT).show()
        );

        // Optional: back button functionality
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void fetchJobDetails(int jobId) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL + jobId) // Replace with your real endpoint
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Activity_Single_Job_Offer.this, "Failed to fetch job details", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject job = new JSONObject(responseData);

                        String title = job.getString("title");
                        String description = job.getString("description");
                        String location = job.getString("location");
                        JSONArray missions = job.getJSONArray("missions");

                        runOnUiThread(() -> {
                            txtTitle.setText(title);
                            txtDescription.setText(description);
                            txtLocation.setText(location);
                            if (missions.length() > 0) txtMission1.setText(missions.optString(0));
                            if (missions.length() > 1) txtMission2.setText(missions.optString(1));
                            if (missions.length() > 2) txtMission3.setText(missions.optString(2));
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
