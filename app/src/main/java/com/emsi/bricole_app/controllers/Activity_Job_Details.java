    package com.emsi.bricole_app.controllers;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.TextView;

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

    public class Activity_Job_Details extends Drawer {

        private final String TAG = "Activity_Job_Details";
        private final String API_URL_BASE = "http://10.0.2.2:8080/api/main/jobs/search/";

        private TextView txtPostedBy, txtTitle, txtLocation, txtMission1, txtMission2, txtMission3, txtDescription;
        private ViewPager2 imageCarousel;
        private Button btnUpdate, btnDelete;
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
            imageCarousel = findViewById(R.id.imageCarousel);

            btnUpdate = findViewById(R.id.btnUpdate);
            btnDelete = findViewById(R.id.btnDelete);

            prefs = getSharedPreferences("auth", MODE_PRIVATE);
            USER_ACCESS_TOKEN = prefs.getString("access_token", null);

            txtPostedBy.setOnClickListener(view->{
                Intent intent = new Intent(this, Activity_Employer_Profile_Details.class);
                intent.putExtra("employer_id", 1);
                startActivity(intent);
            });

            int offerId = getIntent().getIntExtra("job_id", -1);
            if (offerId != -1) {
                fetchJobDetails(offerId);
            }

            btnUpdate.setOnClickListener(view->{
                Intent intent = new Intent(this, Activity_EditJobOffer.class);
                intent.putExtra("job_id", offerId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(view->{
                if (offerId != -1) {
                    deleteOffer(offerId);
                }
            });
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

                                String title = job.getString("title");
                                String location = job.getString("location");
                                String description = job.getString("description");

                                JSONArray missions = job.getJSONArray("missions");

                                txtTitle.setText(title);
                                txtLocation.setText(location);
                                txtDescription.setText(description);

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
                                    imageCarousel.setAdapter(new ImageCarouselAdapter(Activity_Job_Details.this, imageUrls));
                                }
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

        private void deleteOffer(int offerId) {
            OkHttpClient client = new OkHttpClient();

            String deleteUrl = "http://10.0.2.2:8080/api/employer/job/" + offerId + "/delete";

            Request deleteRequest = new Request.Builder()
                    .url(deleteUrl)
                    .delete()
                    .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                    .build();

            client.newCall(deleteRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Delete offer API call failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Offer deleted successfully");
                        finish();
                    } else {
                        Log.e(TAG, "Delete offer API response not successful: Code " + response.code());
                    }
                }
            });
        }
    }
