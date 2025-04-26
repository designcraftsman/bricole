package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Employee;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_Employer_Profile_Details extends Drawer {

    private TextView userNameTextView, phoneTextView, skillsTextView;
    private ImageView profileImage;

    private static final String TAG = "EmployeeProfile";
    private static final String API_BASE_URL = "http://10.0.2.2:8080/api/main/employer/";
    private static final String IMAGE_BASE_URL = "http://10.0.2.2:8080/images/profile/";
    private SharedPreferences prefs;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private String USER_ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile_details);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        userNameTextView = findViewById(R.id.userName);
        phoneTextView = findViewById(R.id.phone);
        skillsTextView = findViewById(R.id.skillsTextView);
        profileImage = findViewById(R.id.profileImage);

        Intent intent = getIntent();
        int employeeId = intent.getIntExtra("employee_id", -1);

        if (employeeId != -1) {
            fetchEmployeeProfile(employeeId);
        } else {
            Log.e(TAG, "No job_id passed in intent");
        }
    }

    private void fetchEmployeeProfile(int jobId) {
        String url = API_BASE_URL + jobId;

        // Add the token to the Authorization header
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

                            // Parse JSON response
                            Type employeeType = new TypeToken<Employee>() {}.getType();
                            Employee employee = gson.fromJson(responseBody, employeeType);

                            // Update UI on the main thread
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
        for (String skill : skills) {
            skillsText.append("• ").append(skill).append("\n");
        }
        skillsTextView.setText(skillsText.toString().trim());

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
}
