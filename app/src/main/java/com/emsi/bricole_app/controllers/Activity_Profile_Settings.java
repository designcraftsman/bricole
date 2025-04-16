package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.emsi.bricole_app.R;
import org.json.JSONObject;

public class Activity_Profile_Settings extends Employer_Drawer {

    private ImageButton mBackBtn;
    private LinearLayout mQualificationsSection, mPreferenceSection, mAvailabilitySection;

    private TextView mUserName, mEmail, mPhone, mLocation;
    private ImageView mProfileImage;
    private SharedPreferences prefs;

    private final String API_URL = "http://10.0.2.2:8080/api/profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_profile_settings);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        final String USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        // UI References
        mUserName = findViewById(R.id.userName);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mLocation = findViewById(R.id.location);
        mProfileImage = findViewById(R.id.profileImage);

        // Navigation Elements
        mQualificationsSection = findViewById(R.id.qualificationsSection);
        mPreferenceSection = findViewById(R.id.preferencesSection);
        mAvailabilitySection = findViewById(R.id.availabilitySection);

        mBackBtn = findViewById(R.id.backButton);

        mBackBtn.setOnClickListener(view -> finish());

        mPreferenceSection.setOnClickListener(view -> {
            startActivity(new Intent(this, Activity_Profile_Preference.class));
        });

        mQualificationsSection.setOnClickListener(view -> {
            startActivity(new Intent(this, Activity_Profile_Qualifications.class));
        });

        mAvailabilitySection.setOnClickListener(view -> {
            startActivity(new Intent(this, Activity_Profile_Employee_Availability.class));
        });

        fetchUserProfile(USER_ACCESS_TOKEN);
    }

    private void fetchUserProfile(String token) {


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        String email = response.getString("email");
                        String firstname = response.getString("firstname");
                        String lastname = response.getString("lastname");
                        int prefix = response.getInt("phoneNumberPrefix");
                        String phone = response.getString("phoneNumber");
                        String address = response.getString("address");
                        String profilePicture = response.getString("profilePicture");

                        mUserName.setText(firstname + " " + lastname);
                        mEmail.setText(email);
                        mPhone.setText("+" + prefix + " " + phone);
                        mLocation.setText(address);

                        Glide.with(this)
                                .load(profilePicture)
                                .placeholder(R.drawable.avatar)
                                .into(mProfileImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace(); // You can log or show error UI
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
