package com.emsi.bricole_app.controllers;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.emsi.bricole_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_Single_Job_Offer extends Drawer {
    private ImageView backButton;
    private TextView  txtPostedBy, txtTitle, txtLocation;
    private TextView  txtMission1, txtMission2, txtMission3;
    private TextView  txtDescription;
    private TextView txtContact;

    private int employerId;
    private Button btnApply;

    private ViewPager2 imageCarousel;

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

        // Job info
        txtPostedBy = findViewById(R.id.txtPostedBy); // Add android:id="@+id/txtPostedBy" if needed
        txtTitle = findViewById(R.id.txtTitle);       // Add android:id="@+id/txtTitle"
        txtLocation = findViewById(R.id.txtLocation); // Add android:id="@+id/txtLocation"

        // Mission section
        txtMission1 = findViewById(R.id.txtMission1);
        txtMission2 = findViewById(R.id.txtMission2);
        txtMission3 = findViewById(R.id.txtMission3);

        // Description section
        txtDescription = findViewById(R.id.txtDescription);

        imageCarousel = findViewById(R.id.imageCarousel);

        // Contact
        txtContact = findViewById(R.id.txtContact);



        // Apply button
        btnApply = findViewById(R.id.btnApply);


        btnApply.setOnClickListener(v -> {
            if (jobId != -1) {
                applyToJob(jobId);
            } else {
                Toast.makeText(this, "Invalid job ID!", Toast.LENGTH_SHORT).show();
            }
        });

        txtPostedBy.setOnClickListener(view->{
            Intent intent = new Intent(Activity_Single_Job_Offer.this, Activity_Employer_Profile_Details.class);
            intent.putExtra("employer_id", employerId);
            startActivity(intent);
        });

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
                        employerId = job.getInt("employerId");
                        JSONArray mediaJson = job.getJSONObject("media").names();
                        List<String> imageUrls = new ArrayList<>();

                        if (mediaJson != null) {
                            JSONObject media = job.getJSONObject("media");

                            for (int i = 0; i < mediaJson.length(); i++) {
                                String key = mediaJson.getString(i);
                                String fileName = media.getString(key);

                                try {
                                    String imageUrl = "http://10.0.2.2:8080/images/jobmedias/"
                                            + URLEncoder.encode(fileName, "UTF-8")
                                            + "?t=" + System.currentTimeMillis(); // Bust cache

                                    imageUrls.add(imageUrl);

                                    // Log each image URL to track it
                                    Log.d(TAG, "Image URL: " + imageUrl);
                                } catch (UnsupportedEncodingException e) {
                                    Log.e(TAG, "Encoding error for file name: " + fileName, e);
                                }
                            }
                        }

                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            imageCarousel.setAdapter(new ImageCarouselAdapter(Activity_Single_Job_Offer.this, imageUrls));
                        }

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

    private void applyToJob(int jobId) {
        // Load access token from SharedPreferences
        String accessToken = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("access_token", null);

        if (accessToken == null) {
            Toast.makeText(this, "Access token not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        String postUrl = "http://10.0.2.2:8080/api/employee/apply/job/" + jobId;

        Request request = new Request.Builder()
                .url(postUrl)
                .post(okhttp3.RequestBody.create(new byte[0])) // empty POST body
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Activity_Single_Job_Offer.this, "Failed to apply for job", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(Activity_Single_Job_Offer.this, "Vous avez postulé avec succès !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Activity_Single_Job_Offer.this, "Échec de la candidature: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
