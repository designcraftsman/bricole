package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Employer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_Employer_Profile_Details extends Drawer {

    private TextView userNameTextView, phoneTextView, email,phone,location;
    private ImageView profileImage;
    private int employerId;
    private static final String TAG = "EmployerProfile";
    private static final String API_BASE_URL = "http://10.0.2.2:8080/api/main/employer/";
    private static final String IMAGE_BASE_URL = "http://10.0.2.2:8080/images/profile/";
    private SharedPreferences prefs;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private LinearLayout msgButton;
    private String USER_ACCESS_TOKEN;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_employer_profile_details); // Keep layout if same, or rename if needed

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        userNameTextView = findViewById(R.id.userName);
        phoneTextView = findViewById(R.id.phone);
        profileImage = findViewById(R.id.profileImage);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        location = findViewById(R.id.location);
        msgButton = findViewById(R.id.messageBar);


        msgButton.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Employer_Profile_Details.this, Activity_Chat_Conversation.class);
            intent.putExtra("receiver_id",employerId);
            intent.putExtra("isNewConversation", true);
            intent.putExtra("user2_name", userNameTextView.getText());
            intent.putExtra("user2_image", phoneTextView.getText());
            startActivity(intent);
        });

        Intent intent = getIntent();
        employerId = intent.getIntExtra("employer_id", -1);

        if (employerId != -1) {
            fetchEmployerProfile(employerId);
        } else {
            Log.e(TAG, "No employer_id passed in intent");
        }
    }

    private void fetchEmployerProfile(int employerId) {
        String url = API_BASE_URL + employerId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                    Employer employer = gson.fromJson(jsonObject.get("data"), Employer.class);
                    new Handler(Looper.getMainLooper()).post(() -> updateUI(employer));
                } else {
                    Log.e(TAG, "Unsuccessful response");
                }
            }
        });
    }

    private void updateUI(Employer employer) {

        userNameTextView.setText(employer.firstname + " " + employer.lastname);
        phoneTextView.setText("+" + employer.phoneNumberPrefix + " " + employer.phoneNumber);
        phone.setText("+" + employer.phoneNumberPrefix + " " + employer.phoneNumber);
        location.setText(employer.address);
        email.setText(employer.email);
        try {
            String imageUrl = IMAGE_BASE_URL +
                    URLEncoder.encode(employer.profilePicture, "UTF-8") +
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
