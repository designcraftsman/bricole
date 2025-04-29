package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Employee;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_Employee_Profile_Details extends Drawer {

    private TextView userNameTextView, phoneTextView, skillsTextView, availabilityTextView;
    private ImageView profileImage;
    private EditText reviewInput;
    private Button submitReviewButton;

    private static final String TAG = "EmployeeProfile";
    private static final String API_BASE_URL = "http://10.0.2.2:8080/api/main/employees/";
    private static final String REVIEW_BASE_URL = "http://10.0.2.2:8080/api/employer/employee/";
    private static final String IMAGE_BASE_URL = "http://10.0.2.2:8080/images/profile/";
    private SharedPreferences prefs;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private String USER_ACCESS_TOKEN;
    private int employeeId = -1;
    private TextView employeeAverageRating;
    private ImageView[] starViews;
    private int selectedRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_employee_profile_details);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        userNameTextView = findViewById(R.id.userName);
        phoneTextView = findViewById(R.id.phone);
        skillsTextView = findViewById(R.id.skillsTextView);
        profileImage = findViewById(R.id.profileImage);
        availabilityTextView = findViewById(R.id.availabilityTextView);
        reviewInput = findViewById(R.id.reviewMessageInput);
        employeeAverageRating = findViewById(R.id.employeeAverageRating);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employee_id", -1);

        if (employeeId != -1) {
            fetchEmployeeProfile(employeeId);
        } else {
            Log.e(TAG, "No employee id passed in intent");
        }
        setupStarRating();

        submitReviewButton.setOnClickListener(view -> {
            String reviewText = reviewInput.getText().toString().trim();
            if (!reviewText.isEmpty() && selectedRating > 0) {
                submitReview(employeeId, reviewText, selectedRating);
            } else {
                Toast.makeText(this, "Veuillez entrer un avis et une note", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setupStarRating() {
        starViews = new ImageView[]{
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };

        for (int i = 0; i < starViews.length; i++) {
            final int index = i;
            starViews[i].setOnClickListener(v -> animateStarSelection(index + 1));
        }
    }

    private void animateStarSelection(int rating) {
        selectedRating = rating;

        for (int i = 0; i < starViews.length; i++) {
            final ImageView star = starViews[i];

            if (i < rating) {
                star.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            star.setImageResource(R.drawable.star_filled);
                            star.setAlpha(0f);
                            star.animate().alpha(1f).setDuration(150).start();
                        }).start();
            } else {
                star.setImageResource(R.drawable.star);
            }
        }
    }

    private void fetchEmployeeProfile(int id) {
        String url = API_BASE_URL + id;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "API call failed", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            Type employeeType = new TypeToken<Employee>() {}.getType();
                            Employee employee = gson.fromJson(responseBody, employeeType);
                            new Handler(Looper.getMainLooper()).post(() -> updateUI(employee));
                        } else {
                            Log.e(TAG, "Unsuccessful response");
                        }
                    }
                });
    }

    private void updateUI(Employee employee) {
        userNameTextView.setText(employee.firstname + " " + employee.lastname);
        phoneTextView.setText("+" + employee.phoneNumberPrefix + " " + employee.phoneNumber);

        List<String> skills = employee.skills;
        StringBuilder skillsText = new StringBuilder();
        if (skills != null && !skills.isEmpty()) {
            for (String skill : skills) {
                skillsText.append("• ").append(skill).append("\n");
            }
            skillsTextView.setText(skillsText.toString().trim());
        } else {
            skillsTextView.setText("No skills listed");
        }

        Map<String, String> availabilityMap = employee.availabilityMap;
        StringBuilder availabilityText = new StringBuilder();
        if (availabilityMap != null && !availabilityMap.isEmpty()) {
            for (Map.Entry<String, String> entry : availabilityMap.entrySet()) {
                availabilityText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            availabilityTextView.setText(availabilityText.toString().trim());
        } else {
            availabilityTextView.setText("No availability info");
        }

        if (employee.reviews != null && !employee.reviews.isEmpty()) {
            double averageRating = employee.reviews.get(0).reviewedEmployee.averageRating;
            employeeAverageRating.setText(String.valueOf(averageRating));
        } else {
            employeeAverageRating.setText("No rating");
        }

        try {
            String imageUrl = IMAGE_BASE_URL +
                    URLEncoder.encode(employee.profilePictureUrl, "UTF-8") +
                    "?t=" + System.currentTimeMillis();

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.avatar)
                    .into(profileImage);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void submitReview(int employeeId, String review, int rating) {
        String url = REVIEW_BASE_URL + employeeId + "/review";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject reviewObject = new JSONObject();
        try {
            reviewObject.put("employeeId", employeeId);
            reviewObject.put("review", review);
            reviewObject.put("rating", rating);
        } catch (Exception e) {
            Log.e(TAG, "JSON creation failed", e);
            return;
        }

        RequestBody body = RequestBody.create(reviewObject.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Review submission failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Review submitted successfully");
                    new Handler(Looper.getMainLooper()).post(() ->
                            reviewInput.setText("Avis envoyé !"));
                } else {
                    Log.e(TAG, "Review submission failed with code: " + response.code());
                }
            }
        });
    }
}
